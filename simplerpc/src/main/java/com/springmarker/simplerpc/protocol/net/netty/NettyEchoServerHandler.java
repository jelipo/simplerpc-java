package com.springmarker.simplerpc.protocol.net.netty;

import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.CompletableFuture;

/**
 * 主要用于处理Netty的Handler
 *
 * @author Springmarker
 * @date 2019/6/13 12:01
 */
public class NettyEchoServerHandler extends ChannelInboundHandlerAdapter {

    private ProxyServerCore proxyServerCore;

    private DataSerialization dataSerialization;

    public NettyEchoServerHandler(ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        this.proxyServerCore = proxyServerCore;
        this.dataSerialization = dataSerialization;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        ExchangeRequest exchangeRequest = dataSerialization.deserializeRequest(in.array());
        RpcRequest rpcRequest = exchangeRequest.getRpcRequest();
        //判断是否是异步请求
        if (rpcRequest.getAsync() == 0) {
            RpcResponse rpcResponse = proxyServerCore.handleMethod(rpcRequest);
            returnResult(ctx, rpcResponse);
        } else {
            CompletableFuture<RpcResponse> future = new CompletableFuture<>();
            proxyServerCore.handleAsyncMethod(rpcRequest, future);
            future.whenComplete((rpcResponse, throwable) -> {
                returnResult(ctx, rpcResponse);
            });
        }
        //TODO 能否使用异步待更改
    }

    private void returnResult(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        //TODO 返回值

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
