package com.springmarker.simplerpc.protocol.net.http;

import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.core.server.AbstractServerHandler;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.ServerConfig;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Frank
 * @date 2018/10/28 20:59
 */
public class HttpServerHandler extends AbstractServerHandler {

    public HttpServerHandler(ServerConfig config, ProxyServerCore proxyServerCore) {
        super(config, proxyServerCore);
    }

    @Override
    public void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(super.config.getPort()), 0);
        super.proxyServerCore.handleMethod(
                new ExchangeRequest(1, 1,
                        new RpcRequest(32, null, 0)));
    }

}