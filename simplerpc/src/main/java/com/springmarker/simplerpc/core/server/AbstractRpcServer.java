package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.ServerConfig;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;

/**
 * 服务端处理器的抽象类，主要用来定义构造方法。
 *
 * @author Springmarker
 * @date 2018/10/28 22:40
 */
public abstract class AbstractRpcServer {

    protected ServerConfig config;

    protected ProxyServerCore proxyServerCore;

    protected DataSerialization dataSerialization;

    public AbstractRpcServer(ServerConfig config, ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        this.config = config;
        this.proxyServerCore = proxyServerCore;
        this.dataSerialization = dataSerialization;
    }

    /**
     * 会单独开启一个线程，启动服务端处理器。
     * 此方法不阻塞。
     *
     * @throws Exception 启动服务时所可能造成的异常。
     */
    public abstract void start() throws Exception;

}