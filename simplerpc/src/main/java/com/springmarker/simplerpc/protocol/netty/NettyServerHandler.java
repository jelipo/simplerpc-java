package com.springmarker.simplerpc.protocol.netty;

import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.core.server.ServerHandler;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.ServerConfig;
import com.springmarker.simplerpc.protocol.netty.pojo.NettyServerConfig;

import java.util.ArrayList;

/**
 * @author Frank
 * @date 2018/10/28 22:58
 */
class NettyServerHandler implements ServerHandler {

    private ProxyServerCore proxyServerCore;

    private void handle() {
        RpcRequest rpcRequest = new RpcRequest(0, new ArrayList(), 1);
        var request = new ExchangeRequest(0, 0, rpcRequest);
        proxyServerCore.handleMethod(request);
    }


    @Override
    public void start(ServerConfig config, ProxyServerCore proxyServerCore) {
        var nettyConf = (NettyServerConfig) config;
        this.proxyServerCore = proxyServerCore;
    }
}