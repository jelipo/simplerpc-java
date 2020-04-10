package com.jelipo.simplerpc.protocol.net.socket.server;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Jelipo
 * @date 2019/6/23 22:01
 */
public interface NettyExceptionWorker {

    //void exception(ChannelHandlerContext ctx, int exceptionType,String exceptionMessage);


    void exception(ChannelHandlerContext ctx, int exceptionType, String exceptionMessage, Long rpcId);

}
