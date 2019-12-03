package com.jelipo.simplerpc.core.server;

import com.jelipo.simplerpc.pojo.ExceptionType;
import com.jelipo.simplerpc.pojo.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author Jelipo
 * @date 2018/10/28 21:00
 */
public class Receiver {

    private RpcServerFactory rpcServerFactory;

    public Receiver(RpcServerFactory rpcServerFactory) {
        this.rpcServerFactory = rpcServerFactory;
    }

    /**
     * 同步执行的主要方法。
     *
     * @param methodHashCode Interface的方法的识别码。
     * @param paramList      参数List。
     * @return
     */
    RpcResponse receive(int methodHashCode, ArrayList<Object> paramList) {
        Method method = rpcServerFactory.getImplMethodByInterfaceMethodHashcode(methodHashCode);
        //没有相应的方法，直接返回。
        if (method == null) {
            return new RpcResponse(null, ExceptionType.NO_SUCHMETHOD_EXCEPTION);
        }
        Object obj = rpcServerFactory.getImplObjectByInterfaceClass(method.getDeclaringClass());
        RpcResponse rpcResponse = new RpcResponse();
        try {
            Object result = method.invoke(obj, paramList.toArray());
            rpcResponse.setResult(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            rpcResponse.setException(ExceptionType.RPC_METHOD_EXCEPTION);
        }
        return rpcResponse;
    }

    /**
     * 异步执行的主要方法。
     *
     * @param methodHashCode Interface的方法的识别码。
     * @param paramList      参数List。
     * @param future         传递过来的异步 {@link CompletableFuture}。用于给内部RPC传递结果。
     */
    void receiveAsync(int methodHashCode, ArrayList<Object> paramList, CompletableFuture<RpcResponse> future) {
        Method method = rpcServerFactory.getImplMethodByInterfaceMethodHashcode(methodHashCode);
        //没有相应的方法，直接对传递过来的future执行完成。
        if (method == null) {
            future.complete(new RpcResponse(null, ExceptionType.NO_SUCHMETHOD_EXCEPTION));
            return;
        }
        Object obj = rpcServerFactory.getImplObjectByInterfaceClass(method.getDeclaringClass());
        RpcResponse rpcResponse = new RpcResponse();
        CompletableFuture futureResult;
        try {
            futureResult = (CompletableFuture) method.invoke(obj, paramList.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            rpcResponse.setException(ExceptionType.RPC_METHOD_EXCEPTION);
            future.complete(rpcResponse);
            return;
        }
        futureResult.whenComplete((BiConsumer<Object, Throwable>) (result, throwable) -> {
            if (throwable != null) {
                rpcResponse.setException(ExceptionType.RPC_METHOD_EXCEPTION);
                future.complete(rpcResponse);
            } else {
                rpcResponse.setException(ExceptionType.NO_EXCEPTION);
                rpcResponse.setResult(result);
                future.complete(rpcResponse);
            }

        });
    }
}