package com.springmarker.simplerpc.protocol.net.netty;

import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.ExchangeResponse;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.CompletableFuture;

/**
 * 主要用于处理Netty的Handler
 *
 * @author Springmarker
 * @date 2019/6/13 12:01
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private ProxyServerCore proxyServerCore;

    private DataSerialization dataSerialization;

    public NettyServerHandler(ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        this.proxyServerCore = proxyServerCore;
        this.dataSerialization = dataSerialization;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {

            ByteBuf in = (ByteBuf) msg;

            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            ExchangeRequest exchangeRequest = dataSerialization.deserializeRequest(bytes);
            RpcRequest rpcRequest = exchangeRequest.getRpcRequest();
            int nettyId = exchangeRequest.getId();
            //判断是否是异步请求
            if (rpcRequest.getAsync() == 0) {
                RpcResponse rpcResponse = proxyServerCore.handleMethod(rpcRequest);

                returnResult(ctx, new ExchangeResponse(nettyId, rpcResponse));
            } else {
                CompletableFuture<RpcResponse> future = new CompletableFuture<>();
                proxyServerCore.handleAsyncMethod(rpcRequest, future);
                future.whenComplete((rpcResponse, throwable) -> {
                    returnResult(ctx, new ExchangeResponse(nettyId, rpcResponse));
                });
            }
            //TODO 能否使用异步待更改
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    /**
     * 此方法主要把rpcResponse序列化为bytes，并通过Netty发送。
     *
     * @param ctx
     * @param exchangeResponse
     */
    private void returnResult(ChannelHandlerContext ctx, ExchangeResponse exchangeResponse) {
        byte[] rpcResponseBytes = null;
        try {
            rpcResponseBytes = dataSerialization.serialize(exchangeResponse);
        } catch (SerializationException e) {
            try {
                exchangeResponse.getRpcResponse().setResult(null);
                exchangeResponse.getRpcResponse().setException(2);
                rpcResponseBytes = dataSerialization.serialize(exchangeResponse);
            } catch (SerializationException ex) {
                ex.printStackTrace();
            }
        }
        ctx.writeAndFlush(Unpooled.copiedBuffer(rpcResponseBytes));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    /**
     * 传输出现异常时，返回rpcResponse。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExchangeResponse exchangeResponse = new ExchangeResponse(-1, new RpcResponse(null, 2));
        returnResult(ctx, exchangeResponse);
        cause.printStackTrace();
    }
}
