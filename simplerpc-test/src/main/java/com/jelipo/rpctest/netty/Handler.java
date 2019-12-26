package com.jelipo.rpctest.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Jelipo
 * @date 2019/12/24 22:12
 */
@ChannelHandler.Sharable
public class Handler extends ChannelInboundHandlerAdapter {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println(evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBufInputStream byteBufInputStream = new ByteBufInputStream((ByteBuf) msg);
        byte[] bytes = new byte[1024];
        for (; ; ) {
            int read = byteBufInputStream.read(bytes);


            if (read != 1024) {
                break;
            }
        }

    }

    /**
     * 传输出现异常时，返回rpcResponse。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

}
