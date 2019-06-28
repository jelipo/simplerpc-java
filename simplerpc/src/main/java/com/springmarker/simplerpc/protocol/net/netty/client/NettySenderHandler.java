package com.springmarker.simplerpc.protocol.net.netty.client;

import com.google.common.cache.Cache;
import com.springmarker.simplerpc.exception.RemoteCallException;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.ExceptionType;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.ExchangeResponse;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.CompletableFuture;

/**
 * 专门处理Netty接收到的信息的Handler。
 *
 * @author Springmarker
 * @date 2019/6/15 12:50
 */
@ChannelHandler.Sharable
class NettySenderHandler extends ChannelInboundHandlerAdapter {

    private DataSerialization dataSerialization;
    private Cache<Integer, CompletableFuture<Object>> cache;

    NettySenderHandler(DataSerialization dataSerialization, Cache<Integer, CompletableFuture<Object>> cache) {
        this.dataSerialization = dataSerialization;
        this.cache = cache;
    }


    private ByteBuf heartBeatByteBuf;

    private ByteBuf getHeartBeatByteBuf() throws SerializationException {
        if (heartBeatByteBuf == null) {
            ExchangeRequest exchangeRequest = new ExchangeRequest(0, 0, 0, null);
            ByteBuf heartBeatByteBuf = Unpooled.copiedBuffer(dataSerialization.serialize(exchangeRequest));
            this.heartBeatByteBuf = heartBeatByteBuf.asReadOnly();
        }
        return heartBeatByteBuf;
    }

    /**
     * 在此handler中，此方法主要用于处理发送心跳。
     *
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                ByteBuf heartBeatByteBuf = getHeartBeatByteBuf();
                ctx.writeAndFlush(heartBeatByteBuf.copy());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ExchangeResponse exchangeResponse;
        try {
            ByteBuf bytebuf = (ByteBuf) msg;
            exchangeResponse = dataSerialization.deserializeResponse(new ByteBufInputStream(bytebuf));
        } finally {
            ReferenceCountUtil.release(msg);
        }
        int id = exchangeResponse.getId();
        if (id < 0) {
            System.out.println("有异常。");
            return;
        }
        CompletableFuture<Object> future = cache.getIfPresent(exchangeResponse.getId());
        cache.invalidate(exchangeResponse.getId());
        if (future == null) {
            System.out.println("找不到id相对应的缓存，可能已经超时/缓存设置太小。id:" + exchangeResponse.getId());
            return;
        }

        RpcResponse rpcResponse = exchangeResponse.getRpcResponse();
        int exceptionCode = rpcResponse.getException();
        switch (exceptionCode) {
            case ExceptionType.NO_EXCEPTION:
                future.complete(rpcResponse.getResult());
                return;
            case ExceptionType.RPC_METHOD_EXCEPTION:
                future.completeExceptionally(new RemoteCallException("An exception occurred when calling a remote method."));
                return;
            case ExceptionType.RPC_INNER_EXCEPTION:
                future.completeExceptionally(new RemoteCallException("Abnormality in RPC.Pelease check network / object size / supported serialized objects "));
                return;
            case ExceptionType.NO_SUCHMETHOD_EXCEPTION:
                future.completeExceptionally(new NoSuchMethodException("No such method in remote server."));
                return;
            case ExceptionType.SERIALIZED_EXCEPTION:
                future.completeExceptionally(new RemoteCallException("Abnormality in RPC. Serialized exception."));
                return;
            case ExceptionType.NETWORK_TRANSMISSION_EXCEPTION:
                future.completeExceptionally(new RemoteCallException("Abnormality in RPC. Exception occurred during netty transmission."));
                return;
            default:
                future.completeExceptionally(new UnknownError("Unknow remote call exception code: " + rpcResponse.getException()));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

}