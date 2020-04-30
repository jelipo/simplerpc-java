package com.jelipo.simplerpc.protocol.net.socket.server;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Jelipo
 * @date 2019/6/23 22:01
 */
public interface NettyExceptionWorker {

    //void exception(ChannelHandlerContext ctx, int exceptionType,String exceptionMessage);


    /**
     * netty异常worker
     *
     * @param exceptionType    异常类型，使用 {@link com.jelipo.simplerpc.pojo.ExceptionType}
     * @param exceptionMessage 异常信息
     */
    void exception(ChannelHandlerContext ctx, int exceptionType, String exceptionMessage, Long rpcId);

}
