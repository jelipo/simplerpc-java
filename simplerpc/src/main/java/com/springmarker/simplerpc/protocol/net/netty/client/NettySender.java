package com.springmarker.simplerpc.protocol.net.netty.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.exception.RemoteCallException;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcRequest;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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
     * 序列化/反序列化处理器。
     */
    private DataSerialization dataSerialization;

    /**
     * 缓存的时间，单位 秒。
     */
    private int cacheTime = 300;

    /**
     * Netty单次发送/接收 最大的字节数。
     */
    private int nettyMaxFrameLength = 1024 * 1024;

    /**
     * 用于控制速率使用。
     */
    private final Semaphore permit = new Semaphore(Runtime.getRuntime().availableProcessors() * 2);

    /**
     * 用于存放 释放锁对象 的缓存。
     */
    private Cache<Integer, CompletableFuture<Object>> cache = CacheBuilder.newBuilder()
            .maximumSize(Integer.MAX_VALUE)
            .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
            .build();

    /**
     * 此构造方法会直接创建一个与Netty Server 的连接。
     *
     * @param host              需要Netty连接的Host地址。
     * @param port              需要Netty连接的端口。
     * @param dataSerialization 序列化处理器。
     * @throws InterruptedException 启动netty时造成的异常。
     */
    public NettySender(String host, int port, DataSerialization dataSerialization) throws InterruptedException {
        defaultSender(host, port, dataSerialization);
    }

    public NettySender(String host, int port, DataSerialization dataSerialization, int nettyMaxFrameLength) throws InterruptedException {
        this.nettyMaxFrameLength = nettyMaxFrameLength;
        defaultSender(host, port, dataSerialization);
    }

    private void defaultSender(String host, int port, DataSerialization dataSerialization) throws InterruptedException {
        this.dataSerialization = dataSerialization;
        start(host, port, dataSerialization);
    }

    /**
     * @param host
     * @param port
     * @return
     */
    private void start(String host, int port, DataSerialization dataSerialization) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        int lengthFieldLength = 4;
                        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(nettyMaxFrameLength, 0, lengthFieldLength, 0, lengthFieldLength);
                        LengthFieldPrepender prepender = new LengthFieldPrepender(lengthFieldLength);
                        ch.pipeline().addLast(decoder, prepender);
                        ch.pipeline().addLast(new IdleStateHandler(0, 4, 0));
                        ch.pipeline().addLast(new NettySenderHandler(dataSerialization, cache));
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                System.out.println("失败，尝试重连");
                Thread.sleep(2000);
                defaultSender(host, port, dataSerialization);
            }
        });
        // 同步等待启动客户端完成。
        this.channel = channelFuture.channel();
    }

    @Override
    public Object syncSend(RpcRequest rpcRequest) throws Exception {
        CompletableFuture<Object> future = asyncSend(rpcRequest);
        Object result = future.get(10, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) throws Exception {
        ExchangeRequest exchangeRequest = buildExchangeRequest(rpcRequest);
        byte[] bytes = dataSerialization.serialize(exchangeRequest);
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        ChannelFuture channelFuture = channel.write(byteBuf);
        permit.acquire();
        channelFuture.addListener((ChannelFutureListener) future -> {
            permit.release();
            if (future.isSuccess()) {
                cache.put(exchangeRequest.getId(), completableFuture);
            } else {
                completableFuture.completeExceptionally(new RemoteCallException("Rpc failed to write messages."));
            }
        });
        channel.flush();
        return completableFuture;
    }

    /**
     * @param rpcRequest
     * @return 专门为client和server之间网络通讯的包装对象。
     */
    private ExchangeRequest buildExchangeRequest(RpcRequest rpcRequest) {
        return new ExchangeRequest(1, clientId, generateNettyNetId(), rpcRequest);
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

    @Override
    public void close() {
        channel.close();
    }

}
