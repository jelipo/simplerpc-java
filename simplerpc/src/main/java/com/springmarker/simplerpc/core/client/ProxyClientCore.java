package com.springmarker.simplerpc.core.client;

import com.springmarker.simplerpc.annotations.AsynRpc;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


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
        return this.sender.send(method, args);
    }

    /**
     * 处理异步方法
     */
    private Object handleAsynRequest(Object obj, Method method, Object[] args, MethodProxy proxy) {
        return null;
    }

    /**
     * 代理类的主要处理方法
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        var annotations = method.getAnnotation(AsynRpc.class);
        if (annotations == null) {
            return handleSyncRequest(obj, method, args, proxy);
        } else {
            return handleAsynRequest(obj, method, args, proxy);
        }
    }
}