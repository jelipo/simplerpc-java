package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Springmarker
 * @date 2018/10/28 21:00
 */
public class Receiver {

    private RpcServerFactory rpcServerFactory;

    public Receiver(RpcServerFactory rpcServerFactory) {
        this.rpcServerFactory = rpcServerFactory;
    }

    public RpcResponse receive(int methodHashCode, ArrayList<Object> paramList) {
        Method method = rpcServerFactory.getImplMethodByInterfaceMethodHashcode(methodHashCode);
        Object obj = rpcServerFactory.getImplObjectByInterfaceClass(method.getDeclaringClass());
        RpcResponse rpcResponse = new RpcResponse();
        try {
            Object result = method.invoke(obj, paramList.toArray());


            rpcResponse.setResult(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            rpcResponse.setException(1);
        }
        return rpcResponse;
    }

    public void receiveAsync(int methodHashCode, ArrayList<Object> paramList, CompletableFuture<RpcResponse> future) {
        Method method = rpcServerFactory.getImplMethodByInterfaceMethodHashcode(methodHashCode);
        Object obj = rpcServerFactory.getImplObjectByInterfaceClass(method.getDeclaringClass());
        RpcResponse rpcResponse = new RpcResponse();
        CompletableFuture futureResult = null;
        try {
            futureResult = (CompletableFuture) method.invoke(obj, paramList.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            rpcResponse.setException(1);
            future.complete(rpcResponse);
            return;
        }
        futureResult.whenComplete((BiConsumer<Object, Throwable>) (result, throwable) -> {
            if (throwable != null) {
                rpcResponse.setException(1);
                future.complete(rpcResponse);
            } else {

            }

        });
    }
}