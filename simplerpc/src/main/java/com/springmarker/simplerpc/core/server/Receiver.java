package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.RpcRequest;

/**
 * @author Frank
 * @date 2018/10/28 21:00
 */
public class Receiver {

    private RpcServerFactory rpcServerFactory;

    public Receiver(RpcServerFactory rpcServerFactory) {
        this.rpcServerFactory = rpcServerFactory;
    }

    public Object receive(RpcRequest request) {
        Object obj = rpcServerFactory.get(request.getMethodHashCode());
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null;
    }
}