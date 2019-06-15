package com.springmarker.simplerpc.protocol.net.netty;

import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
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

    public NettySender(String host, int port) {
    }

    @Override
    public Object syncSend(RpcRequest rpcRequest) {
        return null;
    }

    @Override
    public CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) {
        return null;
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
     *
     */
    private class NettySenderHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

    }
}
