package com.jelipo.simplerpc.protocol.net.socket.server;

import com.jelipo.simplerpc.pojo.ExceptionType;
import com.jelipo.simplerpc.pojo.ProtocolMeta;
import com.jelipo.simplerpc.pojo.RpcRequest;
import com.jelipo.simplerpc.protocol.net.CommonMetaUtils;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.util.List;

/**
 * 主要用于处理Netty的Handler
 *
 * @author Jelipo
 * @date 2019/6/13 12:01
 */
@ChannelHandler.Sharable
public class NettyServerMainHandler extends ChannelInboundHandlerAdapter {

    private final List<NettyWorker> workerList;

    private final NettyExceptionWorker exceptionWorker;

    private final DataSerialization dataSerialization;

    private final NettyHeartBeatWorker nettyHeartBeatWorker;

    /**
     * 构建一个Netty主处理器
     *
     * @param workerList           worker 的List。如有消息传过来，会按照List顺序交由worker判断是否处理，
     *                             如遇到某个worker可以处理，会返回true。
     * @param exceptionWorker      异常worker
     * @param nettyHeartBeatWorker
     * @param dataSerialization
     */
    public NettyServerMainHandler(List<NettyWorker> workerList,
                                  NettyExceptionWorker exceptionWorker,
                                  NettyHeartBeatWorker nettyHeartBeatWorker,
                                  DataSerialization dataSerialization) {
        this.nettyHeartBeatWorker = nettyHeartBeatWorker;
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
            mainRead(ctx, msg);
        } catch (Throwable e) {
            e.printStackTrace();
            exceptionWorker.exception(ctx, ExceptionType.RPC_INNER_EXCEPTION, e.getMessage(), null);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void mainRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBufInputStream byteBufInputStream = new ByteBufInputStream((ByteBuf) msg);
        short headerLength = byteBufInputStream.readShort();
        byte[] bytes = new byte[headerLength];
        int read = byteBufInputStream.read(bytes);
        ProtocolMeta protocolMeta = CommonMetaUtils.deserialize(bytes);
        if (protocolMeta.isHreatBeat()) {
            nettyHeartBeatWorker.handle(ctx, protocolMeta, null);
            return;
        }
        RpcRequest rpcRequest = dataSerialization.deserializeRequest(byteBufInputStream);
        for (NettyWorker nettyHandlerWorker : workerList) {
            if (nettyHandlerWorker.handle(ctx, protocolMeta, rpcRequest)) {
                break;
            }
        }
    }


    /**
     * 传输出现异常时，返回rpcResponse。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        exceptionWorker.exception(ctx, ExceptionType.RPC_INNER_EXCEPTION, cause.getMessage(), null);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接断开");
        super.channelInactive(ctx);
    }
}
