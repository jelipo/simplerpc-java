package com.springmarker.simplerpc.core.client;

import com.springmarker.simplerpc.annotations.AsyncRpc;
import com.springmarker.simplerpc.pojo.RpcRequest;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author Springmarker
 * @date 2018/10/15 21:23
 */
public class ProxyClientCore implements MethodInterceptor {

    private SenderInterface sender;

    public ProxyClientCore(SenderInterface sender) {
        this.sender = sender;
    }

    /**
     * 处理同步方法
     */
    private Object handleSyncRequest(Object obj, Method method, Object[] args, MethodProxy proxy) {
        RpcRequest rpcRequest = packageRpcRequest(method, args, false);
        return this.sender.syncSend(rpcRequest);
    }

    /**
     * 处理异步方法
     */
    private CompletableFuture handleAsynRequest(Object obj, Method method, Object[] args, MethodProxy proxy) {
        RpcRequest rpcRequest = packageRpcRequest(method, args, false);
        return this.sender.asyncSend(rpcRequest);
    }

    /**
     * 代理类的主要处理方法
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        AsyncRpc annotation = method.getAnnotation(AsyncRpc.class);
        if (annotation == null) {
            return handleSyncRequest(obj, method, args, proxy);
        } else {
            return CompletableFuture.supplyAsync(() -> {
                Object result = handleSyncRequest(obj, method, args, proxy);
                return result;
            });
        }
    }

    private RpcRequest packageRpcRequest(Method method, Object[] args, boolean isAsync) {
        Class<?> returnType = method.getReturnType();
        int needReturn = returnType.equals(Void.TYPE) ? 0 : 1;
        int async = isAsync ? 1 : 0;
        return new RpcRequest(method.hashCode(), new ArrayList(List.of(args)), needReturn, async);
    }
}