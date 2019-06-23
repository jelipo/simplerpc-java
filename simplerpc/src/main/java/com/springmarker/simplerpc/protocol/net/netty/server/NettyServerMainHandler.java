package com.springmarker.simplerpc.protocol.net.netty.server;

import com.springmarker.simplerpc.pojo.ExceptionType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * 主要用于处理Netty的Handler
 *
 * @author Springmarker
 * @date 2019/6/13 12:01
 */
@ChannelHandler.Sharable
public class NettyServerMainHandler extends ChannelInboundHandlerAdapter {


    private final List<NettyWorker> workerList;
    private final NettyExceptionWorker exceptionWorker;

    public NettyServerMainHandler(List<NettyWorker> workerList, NettyExceptionWorker exceptionWorker) {
        this.workerList = workerList;
        this.exceptionWorker = exceptionWorker;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        for (NettyWorker nettyHandlerWorker : workerList) {
            nettyHandlerWorker.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            byte[] bytes = toBytes(msg);
            if (bytes == null) {
                return;
            }
            for (NettyWorker nettyHandlerWorker : workerList) {
                if (nettyHandlerWorker.handle(ctx, bytes)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exceptionWorker.exception(ctx, ExceptionType.RPC_INNER_EXCEPTION);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 传输出现异常时，返回rpcResponse。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        exceptionWorker.exception(ctx, ExceptionType.RPC_INNER_EXCEPTION);
    }

    /**
     * 可将Netty的ByteBuf类型转换为byte数组并返回。
     * 如果遇到类型为byte[]，直接返回。
     * 如果无法转换，直接返回null。
     */
    private static byte[] toBytes(Object msg) {
        byte[] bytes = null;
        if (msg instanceof ByteBuf) {
            ByteBuf in = (ByteBuf) msg;
            bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
        } else if (msg instanceof byte[]) {
            bytes = (byte[]) msg;
        }
        return bytes;
    }

}
