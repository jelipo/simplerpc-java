package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Springmarker
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

            //针对返回值为CompletableFuture类型的结果特殊处理，等待其完成后获取结果后再继续执行。
            if (result instanceof CompletableFuture) {
                result = ((CompletableFuture) result).get();
            }

            rpcResponse.setResult(result);
        } catch (IllegalAccessException | InvocationTargetException | ExecutionException e) {
            rpcResponse.setException(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rpcResponse;
    }
}