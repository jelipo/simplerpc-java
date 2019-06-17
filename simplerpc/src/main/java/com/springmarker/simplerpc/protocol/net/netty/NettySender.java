package com.springmarker.simplerpc.protocol.net.netty;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.exception.RemoteCallException;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.ExchangeResponse;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用Netty的主要发送类。
 * 此类保证线程安全。
 *
 * @author: Springmarker
 * @date: 2019/6/11 2:51
 */
public class NettySender implements SenderInterface {

    /**
     * 每个NettySender实体都有一个随机生成的id，作为CLientId使用。
     */
    private final int clientId = new Random(Integer.MAX_VALUE).nextInt();

    /**
     * 用于发送Netty消息的Channel。
     */
    private Channel channel;

    /**
     * 序列化处理。
     */
    private DataSerialization dataSerialization;

    private int cacheTime = 300;

    /**
     * 用于存放 释放锁对象 的缓存。
     */
    private Cache<Integer, CompletableFuture<Object>> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
            .removalListener((RemovalListener<Integer, CompletableFuture<Object>>) (key, value, cause) -> {
                value.completeExceptionally(new TimeoutException("Cache timeout."));
            })
            .build();

    /**
     * 此构造方法会直接创建一个与Netty Server 的连接。
     *
     * @param host
     * @param port
     * @param dataSerialization
     * @throws InterruptedException
     */
    public NettySender(String host, int port, DataSerialization dataSerialization) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(10 * 1024, 0, 4, 0, 4);
                        LengthFieldPrepender prepender = new LengthFieldPrepender(4);
                        ch.pipeline().addLast(decoder);
                        ch.pipeline().addLast(prepender);
                        ch.pipeline().addLast(new NettySenderHandler(dataSerialization, cache));
                    }
                });
        // 启动客户端
        Channel channel = bootstrap.connect(host, port).sync().channel();
        this.channel = channel;
        this.dataSerialization = dataSerialization;
    }

    @Override
    public Object syncSend(RpcRequest rpcRequest) throws Exception {
        CompletableFuture<Object> future = asyncSend(rpcRequest);
        return future.get(10, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) throws Exception {
        ExchangeRequest exchangeRequest = buildExchangeRequest(rpcRequest);
        byte[] bytes = dataSerialization.serialize(exchangeRequest);
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        CompletableFuture<Object> future = new CompletableFuture<>();
        cache.put(exchangeRequest.getId(), future);
        ChannelFuture channelFuture = channel.writeAndFlush(byteBuf).sync();
        return future;
    }

    /**
     * @param rpcRequest
     * @return 专门为client和server之间网络通讯的包装对象。
     */
    private ExchangeRequest buildExchangeRequest(RpcRequest rpcRequest) {
        return new ExchangeRequest(clientId, generateNettyNetId(), rpcRequest);
    }

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 生成Netty通讯的ID，方便识别往复的内容。
     *
     * @return id。
     */
    private int generateNettyNetId() {
        if (atomicInteger.get() >= Integer.MAX_VALUE) {
            atomicInteger.set(1);
            return 1;
        }
        return atomicInteger.addAndGet(1);
    }


    /**
     * 专门处理Netty接收到的信息的Handler。
     */
    private class NettySenderHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private DataSerialization dataSerialization;
        private Cache<Integer, CompletableFuture<Object>> cache;

        NettySenderHandler(DataSerialization dataSerialization, Cache<Integer, CompletableFuture<Object>> cache) {
            this.dataSerialization = dataSerialization;
            this.cache = cache;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            byte[] bytes = new byte[msg.readableBytes()];
            msg.readBytes(bytes);
            ExchangeResponse exchangeResponse = dataSerialization.deserializeResponse(bytes);
            int id = exchangeResponse.getId();
            if (id < 0) {
                System.out.println("有异常。");
                return;
            }
            CompletableFuture<Object> future = cache.getIfPresent(exchangeResponse.getId());
            if (future == null) {
                System.out.println("可能已经超时。");
                return;
            }
            RpcResponse rpcResponse = exchangeResponse.getRpcResponse();
            if (rpcResponse.getException() == 0) {
                future.complete(rpcResponse.getResult());
            } else if (exchangeResponse.getRpcResponse().getException() == 1) {
                future.completeExceptionally(new RemoteCallException("An exception occurred when calling a remote method."));
            } else if (rpcResponse.getException() == 2) {
                future.completeExceptionally(new RemoteCallException("Abnormality in RPC.Pelease check network / object size / supported serialized objects "));
            } else {
                future.completeExceptionally(new UnknownError("Unknow remote call exception code: " + rpcResponse.getException()));
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

    }
}
