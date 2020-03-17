package com.jelipo.simplerpc.protocol.net.socket.client;

import com.google.common.primitives.Shorts;
import com.jelipo.simplerpc.pojo.ProtocolMeta;
import com.jelipo.simplerpc.pojo.RpcResponse;
import com.jelipo.simplerpc.protocol.common.RpcExceptionFilter;
import com.jelipo.simplerpc.protocol.common.cache.IdFutureCache;
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

    private IdFutureCache idFutureCache;

    NettySenderHandler(NettyClientContext clientContext) {
        this.dataSerialization = clientContext.getDataSerialization();
        this.idFutureCache = clientContext.getIdFutureCache();
    }

    private ByteBuf heartBeatByteBuf;

    private ByteBuf getHeartBeatByteBuf() {
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
            CompletableFuture<Object> future = idFutureCache.getAndDelete(id);
            if (future == null) {
                System.out.println("找不到id相对应的缓存，可能已经超时/缓存设置太小。id:" + id);
                return;
            }
            int exceptionCode = rpcResponse.getException();
            Exception exception = RpcExceptionFilter.filter(exceptionCode);
            //如有异常，则直接向 Future 中写入异常
            if (exception != null) {
                future.completeExceptionally(exception);
                return;
            }
            future.complete(rpcResponse.getResult());
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