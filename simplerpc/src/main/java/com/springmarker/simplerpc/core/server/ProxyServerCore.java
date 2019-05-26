package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.pojo.ServerConfig;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author Springmarker
 * @date 2018/10/15 21:23
 */
public class ProxyServerCore {

    private Receiver receiver;

    public ProxyServerCore(Receiver receiver) {
        this.receiver = receiver;
    }

    public RpcResponse handleMethod(ExchangeRequest baseExchangeRequest) {
        return receiver.receive(baseExchangeRequest.getRpcRequest());
    }

}