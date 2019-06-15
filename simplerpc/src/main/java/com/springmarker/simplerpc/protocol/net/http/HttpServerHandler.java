package com.springmarker.simplerpc.protocol.net.http;

import com.springmarker.simplerpc.core.server.AbstractServer;
import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.exception.DeserializationException;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.pojo.ServerConfig;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * @author Springmarker
 * @date 2018/10/28 20:59
 */
public class HttpServerHandler extends AbstractServer {


    public HttpServerHandler(ServerConfig config, ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        super(config, proxyServerCore, dataSerialization);
    }

    @Override
    public void start() throws IOException {
        new Thread(() -> {
            HttpServer httpServer = null;
            try {
                httpServer = HttpServer.create(new InetSocketAddress(super.config.getPort()), 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpServer.createContext("/", exchange -> {

                byte[] bytes = exchange.getRequestBody().readAllBytes();
                ExchangeRequest exchangeRequest;
                try {
                    exchangeRequest = this.dataSerialization.deserializeRequest(bytes);
                    RpcResponse rpcResponse = new RpcResponse("1", 0);
                    byte[] serialize = dataSerialization.serialize(rpcResponse);
                    exchange.sendResponseHeaders(200, serialize.length);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(serialize);
                    responseBody.flush();
                    responseBody.close();

                } catch (DeserializationException | SerializationException e) {
                    e.printStackTrace();
                }

            });
            httpServer.start();
        }).start();

    }

}