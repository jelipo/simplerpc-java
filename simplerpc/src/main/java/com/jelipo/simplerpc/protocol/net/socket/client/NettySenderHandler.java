package com.jelipo.simplerpc.protocol.net.socket.client;

import com.google.common.cache.Cache;
import com.google.common.primitives.Shorts;
import com.jelipo.simplerpc.exception.RemoteCallException;
import com.jelipo.simplerpc.exception.SerializationException;
import com.jelipo.simplerpc.pojo.ExceptionType;
import com.jelipo.simplerpc.pojo.ProtocolMeta;
import com.jelipo.simplerpc.pojo.RpcResponse;
import com.jelipo.simplerpc.protocol.net.CommonMetaUtils;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
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
 * @author Jelipo
 * @date 2019/6/15 12:50
 */
@ChannelHandler.Sharable
class NettySenderHandler extends ChannelInboundHandlerAdapter {

    private DataSerialization dataSerialization;
    private Cache<Integer, CompletableFuture<Object>> cache;

    NettySenderHandler(NettyClientContext clientContext) {
        this.dataSerialization = clientContext.getDataSerialization();
        this.cache = clientContext.getCache();
    }

    private ByteBuf heartBeatByteBuf;

    private ByteBuf getHeartBeatByteBuf() throws SerializationException {
        if (heartBeatByteBuf == null) {
            byte[] metaBytes = CommonMetaUtils.toBytes(true, 0, "", null);
            byte[] bytes = Shorts.toByteArray((short) metaBytes.length);
            ByteBuf heartBeatByteBuf = Unpooled.copiedBuffer(bytes, metaBytes);
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
        RpcResponse rpcResponse;
        try {
            ByteBuf bytebuf = (ByteBuf) msg;
            ByteBufInputStream byteBufInputStream = new ByteBufInputStream(bytebuf);

            short headerLength = byteBufInputStream.readShort();
            byte[] bytes = new byte[headerLength];
            int read = byteBufInputStream.read(bytes);
            ProtocolMeta protocolMeta = CommonMetaUtils.deserialize(bytes);
            rpcResponse = dataSerialization.deserializeResponse(byteBufInputStream);

            int id = protocolMeta.getRpcId();
            if (id < 0) {
                System.out.println("有异常。");
                return;
            }

            CompletableFuture<Object> future = cache.getIfPresent(id);
            cache.invalidate(id);
            if (future == null) {
                System.out.println("找不到id相对应的缓存，可能已经超时/缓存设置太小。id:" + id);
                return;
            }
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
        } finally {
            ReferenceCountUtil.release(msg);
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