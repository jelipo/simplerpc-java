package com.jelipo.simplerpc.protocol.net.socket.server;

import com.jelipo.simplerpc.pojo.ProtocolMeta;
import com.jelipo.simplerpc.pojo.RpcRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * 为了拆分业务逻辑，故只设置一个NettyHandler和一个Netty编解码协议，所以此接口主要定义Netty不同的内容的处理。
 *
 * @author Jelipo
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
     * @param ctx          Netty的ChannelHandlerContext
     * @param protocolMeta 协议的 META 信息
     * @param rpcRequest   用于rpc client发送给server 交互的Request。
     * @return 是否是此handle所需要的，如果true，说明不需要向后执行worker。
     * @throws Exception 处理期间产生的异常全部捕获。
     */
    boolean handle(ChannelHandlerContext ctx, ProtocolMeta protocolMeta, RpcRequest rpcRequest) throws Exception;


}
