package com.jelipo.simplerpc.protocol.net.socket.server;

import com.jelipo.simplerpc.core.server.ProxyServerCore;
import com.jelipo.simplerpc.exception.SerializationException;
import com.jelipo.simplerpc.pojo.*;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CompletableFuture;

/**
 * 作为使用Netty RPC的主要内容处理类。
 *
 * @author Jelipo
 * @date 2019/6/23 21:48
 */
public class NettyRpcWorker implements NettyWorker, NettyExceptionWorker {

    private final DataSerialization dataSerialization;

    private final ProxyServerCore proxyServerCore;

    public NettyRpcWorker(ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        this.proxyServerCore = proxyServerCore;
        this.dataSerialization = dataSerialization;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    }

    @Override
    public boolean handle(ChannelHandlerContext ctx, ExchangeRequest exchangeRequest) throws Exception {
        RpcRequest rpcRequest = exchangeRequest.getRpcRequest();
        int nettyId = exchangeRequest.getId();
        if (rpcRequest == null) {
            return false;
        }
        //判断是否是异步请求
        if (rpcRequest.getAsync() == 0) {
            RpcResponse rpcResponse = proxyServerCore.handleMethod(rpcRequest);
            returnResult(ctx, new ExchangeResponse(nettyId, rpcResponse));
        } else {
            CompletableFuture<RpcResponse> future = new CompletableFuture<>();
            proxyServerCore.handleAsyncMethod(rpcRequest, future);
            RpcResponse rpcResponse = future.get();
            returnResult(ctx, new ExchangeResponse(nettyId, rpcResponse));
        }
        return true;
    }

    /**
     * 此方法主要把rpcResponse序列化为bytes，并通过Netty发送。
     */
    private void returnResult(ChannelHandlerContext ctx, ExchangeResponse exchangeResponse) {
        byte[] rpcResponseBytes = null;
        try {
            rpcResponseBytes = dataSerialization.serialize(exchangeResponse);
        } catch (SerializationException e) {
            try {
                exchangeResponse.getRpcResponse().setResult(null);
                exchangeResponse.getRpcResponse().setException(ExceptionType.SERIALIZED_EXCEPTION);
                rpcResponseBytes = dataSerialization.serialize(exchangeResponse);
            } catch (SerializationException ex) {
                ex.printStackTrace();
            }
        }
        ctx.writeAndFlush(Unpooled.copiedBuffer(rpcResponseBytes));
    }

    @Override
    public void exception(ChannelHandlerContext ctx, int exceptionType) {
        returnResult(ctx, new ExchangeResponse(-1, new RpcResponse(null, exceptionType)));
    }
}
