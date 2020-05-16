package com.jelipo.simplerpc.protocol.net.socket.server;

import com.google.common.primitives.Shorts;
import com.jelipo.simplerpc.core.server.ProxyServerCore;
import com.jelipo.simplerpc.exception.SerializationException;
import com.jelipo.simplerpc.pojo.ExceptionType;
import com.jelipo.simplerpc.pojo.ProtocolMeta;
import com.jelipo.simplerpc.pojo.RpcRequest;
import com.jelipo.simplerpc.pojo.RpcResponse;
import com.jelipo.simplerpc.protocol.net.CommonMetaUtils;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
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
    public boolean handle(ChannelHandlerContext ctx, ProtocolMeta protocolMeta, RpcRequest rpcRequest) throws Exception {
        int rpcId = protocolMeta.getRpcId();
        if (rpcRequest == null) {
            return false;
        }
        //判断是否是异步请求
        if (rpcRequest.getAsync() == 0) {
            RpcResponse rpcResponse = proxyServerCore.handleMethod(rpcRequest, protocolMeta);
            returnResult(ctx, rpcResponse, rpcId);
        } else {
            CompletableFuture<RpcResponse> future = new CompletableFuture<>();
            proxyServerCore.handleAsyncMethod(rpcRequest, future, protocolMeta);
            future.whenComplete((rpcResponse, throwable) -> {
                returnResult(ctx, rpcResponse, rpcId);
            });
        }
        return true;
    }

    /**
     * 此方法主要把rpcResponse序列化为bytes，并通过Netty发送。
     */
    private void returnResult(ChannelHandlerContext ctx, RpcResponse rpcResponse, int rpcId) {
        byte[] rpcResponseBytes = null;
        byte[] metaBytes = CommonMetaUtils.toBytes(false, rpcId, "", null);
        try {
            rpcResponseBytes = dataSerialization.serialize(rpcResponse);
        } catch (SerializationException e) {
            try {
                rpcResponse.setResult(null);
                rpcResponse.setException(ExceptionType.SERIALIZED_EXCEPTION);
                rpcResponseBytes = dataSerialization.serialize(rpcResponse);
            } catch (SerializationException ex) {
                ex.printStackTrace();
            }
        }
        Channel channel = ctx.channel();
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf byteBuf = Unpooled.copiedBuffer(Shorts.toByteArray((short) metaBytes.length), metaBytes, rpcResponseBytes);
        if (channel.isWritable()) {
            channel.writeAndFlush(byteBuf);
        } else {
            try {
                channel.writeAndFlush(byteBuf).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void exception(ChannelHandlerContext ctx, int exceptionType, String exceptionMessage, Long rpcId) {
        returnResult(ctx, new RpcResponse(null, exceptionType, exceptionMessage), -1);
    }
}
