package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.ServerConfig;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;


/**
 * @author Springmarker
 * @date 2018/10/15 21:23
 */
public class ProxyServerCore{

        private ReceiverInterface receiver;
        private ServerHandler serverHandler;
        private ServerConfig serverConfig;

    public ProxyServerCore(ReceiverInterface receiver, ServerHandler serverHandler, ServerConfig serverConfig) {
        this.receiver = receiver;
        this.serverHandler = serverHandler;
        this.serverConfig = serverConfig;
        serverHandler.start(serverConfig, this);
    }


    public Object handleMethod(ExchangeRequest baseExchangeRequest) {
        return receiver.receive(baseExchangeRequest.getRpcRequest());
    }

    /**
     * 处理同步方法
     */
    private Object handleSyncRequest(Object obj, Method method, Object[] args, MethodProxy proxy) {
        this.receiver.receive(null);
        return null;
    }

}