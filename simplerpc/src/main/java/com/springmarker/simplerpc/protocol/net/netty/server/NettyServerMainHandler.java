package com.springmarker.simplerpc.protocol.net.netty.server;

import com.springmarker.simplerpc.pojo.ExceptionType;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
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

    private final DataSerialization dataSerialization;

    public NettyServerMainHandler(List<NettyWorker> workerList,
                                  NettyExceptionWorker exceptionWorker,
                                  DataSerialization dataSerialization) {
        this.workerList = workerList;
        this.exceptionWorker = exceptionWorker;
        this.dataSerialization = dataSerialization;
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
            ByteBufInputStream byteBufInputStream = new ByteBufInputStream((ByteBuf) msg);
            ExchangeRequest exchangeRequest = dataSerialization.deserializeRequest(byteBufInputStream);
            for (NettyWorker nettyHandlerWorker : workerList) {
                if (nettyHandlerWorker.handle(ctx, exchangeRequest)) {
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

}
