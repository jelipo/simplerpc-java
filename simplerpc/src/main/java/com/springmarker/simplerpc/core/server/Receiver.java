package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Frank
 * @date 2018/10/28 21:00
 */
public class Receiver {

    private RpcServerFactory rpcServerFactory;

    public Receiver(RpcServerFactory rpcServerFactory) {
        this.rpcServerFactory = rpcServerFactory;
    }

    public RpcResponse receive(RpcRequest request) {
        Method method = rpcServerFactory.getImplMethodByInterfaceMethodHashcode(request.getMethodHashCode());
        Object obj = rpcServerFactory.getImplObjectByInterfaceClass(method.getDeclaringClass());
        RpcResponse rpcResponse = new RpcResponse();
        try {
            Object result = method.invoke(obj, request.getParamList());
            rpcResponse.setResult(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            rpcResponse.setException(1);
        }
        return rpcResponse;
    }
}