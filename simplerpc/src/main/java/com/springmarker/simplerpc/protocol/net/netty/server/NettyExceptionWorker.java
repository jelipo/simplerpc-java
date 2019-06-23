package com.springmarker.simplerpc.protocol.net.netty.server;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Springmarker
 * @date 2019/6/23 22:01
 */
public interface NettyExceptionWorker {

    void exception(ChannelHandlerContext ctx, int exceptionType);
}
