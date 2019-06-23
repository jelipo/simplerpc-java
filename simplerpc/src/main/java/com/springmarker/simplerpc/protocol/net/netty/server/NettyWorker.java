package com.springmarker.simplerpc.protocol.net.netty.server;

import io.netty.channel.ChannelHandlerContext;

/**
 * 为了拆分业务逻辑，故只设置一个NettyHandler和一个Netty编解码协议，所以此接口主要定义Netty不同的内容的处理。
 *
 * @author Springmarker
 * @date 2019/6/23 21:35
 */
public interface NettyWorker {

    /**
     * 对应 {@link io.netty.channel.ChannelInboundHandler#userEventTriggered(ChannelHandlerContext, Object)}方法，
     * 具体业务实现都由此方法实现。
     */
    void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    /**
     * 业务逻辑的主要处理方法。
     *
     * @param ctx   Netty的ChannelHandlerContext
     * @param bytes 已经解析好的byte数组，可以直接拿来用。
     * @return 是否是此handle所需要的，如果true，说明不需要向后执行worker。
     */
    boolean handle(ChannelHandlerContext ctx, byte[] bytes) throws Exception;


}
