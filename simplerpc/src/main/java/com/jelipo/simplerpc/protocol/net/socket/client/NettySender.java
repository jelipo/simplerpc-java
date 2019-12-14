package com.jelipo.simplerpc.protocol.net.socket.client;

import com.google.common.cache.Cache;
import com.google.common.primitives.Ints;
import com.jelipo.simplerpc.core.client.RpcSender;
import com.jelipo.simplerpc.exception.RemoteCallException;
import com.jelipo.simplerpc.pojo.ExchangeRequest;
import com.jelipo.simplerpc.pojo.RpcRequest;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用Netty的主要发送类。
 * 此类保证线程安全。
 *
 * @author Jelipo
 * @date 2019/6/11 2:51
 */
public class NettySender implements RpcSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 每个NettySender实体都有一个随机生成的id，作为CLientId使用。
     */
    private final int clientId = new Random(Integer.MAX_VALUE).nextInt();

    /**
     * 用于发送Netty消息的Channel。
     */
    private Channel channel;

    private Cache<Integer, CompletableFuture<Object>> cache;

    private DataSerialization dataSerialization;

    public NettySender(NettyClientContext clientContext, Channel channel) {
        this.channel = channel;
        this.cache = clientContext.getCache();
        this.dataSerialization = clientContext.getDataSerialization();
    }

    /**
     * 用于控制速率使用。
     */
    private final Semaphore permit = new Semaphore(Runtime.getRuntime().availableProcessors() * 2);


    @Override
    public Object syncSend(RpcRequest rpcRequest) throws Exception {
        CompletableFuture<Object> future = asyncSend(rpcRequest);
        Object result = future.get(10, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) throws Exception {
        ExchangeRequest exchangeRequest = buildExchangeRequest(rpcRequest);
        byte[] bytes = dataSerialization.serialize(rpcRequest);
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
