package com.springmarker.simplerpc.protocol.net.netty;

import com.springmarker.simplerpc.core.server.AbstractServerHandler;
import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.pojo.ServerConfig;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;

/**
 * Netty的Server启动类。
 *
 * @author: Springmarker
 * @date: 2019/6/11 2:52
 */
public class NettyServerHandler extends AbstractServerHandler {

    private NettyEchoServerHandler echoServerHandler;

    public NettyServerHandler(ServerConfig config, ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        super(config, proxyServerCore, dataSerialization);
        echoServerHandler = new NettyEchoServerHandler(proxyServerCore, dataSerialization);
    }

    @Override
    public void start() throws Exception {

    }

}
