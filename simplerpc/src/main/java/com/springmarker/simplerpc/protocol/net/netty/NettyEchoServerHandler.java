package com.springmarker.simplerpc.protocol.net.netty;

import com.esotericsoftware.kryo.io.Input;
import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.io.InputStream;

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

        super.channelRead(ctx, msg);
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
