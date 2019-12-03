package com.jelipo.simplerpc.core.client;

import com.jelipo.simplerpc.pojo.RpcRequest;
import com.jelipo.simplerpc.util.MethodHashcodeUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


/**
 * 由Cglib代理Interface的方法的核心处理类。
 * 所有由Cglib代理的Interface都会在此类做拦截并处理。
 *
 * @author Jelipo
 * @date 2018/10/15 21:23
 */
public class ProxyClientCore implements MethodInterceptor {

    private RpcSender sender;

    public ProxyClientCore(RpcSender sender) {
        this.sender = sender;
    }

    /**
     * 处理同步方法
     */
    private Object handleSyncRequest(Object obj, Method method, Object[] args, MethodProxy proxy) throws Exception {
        Object objTemp = checkMethod(method);
        if (objTemp != null) {
            return objTemp;
        }
        RpcRequest rpcRequest = packageRpcRequest(method, args, false);
        return this.sender.syncSend(rpcRequest);
    }

    /**
     * 处理异步方法
     */
    private CompletableFuture handleAsynRequest(Object obj, Method method, Object[] args, MethodProxy proxy) throws Exception {
        RpcRequest rpcRequest = packageRpcRequest(method, args, true);
        return this.sender.asyncSend(rpcRequest);
    }

    /**
     * 代理类的主要处理方法
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
            return handleAsynRequest(obj, method, args, proxy);
        } else {
            return handleSyncRequest(obj, method, args, proxy);
        }
    }

    /**
     * 根据Method方法、参数等做处理包装成一个 {@link RpcRequest}。
     *
     * @param method  某个Interface class的Method。
     * @param args    Method的参数数组。由cglib代理后全是Object类型，需要转换。
     * @param isAsync 是否是异步方法。
     * @return 一个包装好的 {@link RpcRequest} 。
     */
    private RpcRequest packageRpcRequest(Method method, Object[] args, boolean isAsync) {
        ArrayList<Object> paramList = new ArrayList<>(args.length);
        if (args.length != 0) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            //判断是否是基本类型，如果是的就不强制转换。
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> param = parameterTypes[i];
                Object obj = param.isPrimitive() ? args[i] : param.cast(args[i]);
                paramList.add(obj);
            }
        }
        Class<?> returnType = method.getReturnType();
        int needReturn = returnType.equals(Void.TYPE) ? 0 : 1;
        int async = isAsync ? 1 : 0;

        return new RpcRequest(MethodHashcodeUtil.methodHashcode(method), paramList, needReturn, async);
    }

    /**
     * 检查Method是否合规，如果遇到toString之类的Object方法，可能会做拦截处理并直接返回相应的值。
     *
     * @param method
     * @return 如果为null，说明没问题，可以继续执行。2如果不为null，说明method已被拦截并处理，返回值即为处理结果。
     */
    private Object checkMethod(Method method) {
        if ("toString".equals(method.getName())) {
            return "A class Proxyed by Cglib.Class name:" + method.getDeclaringClass().getName();
        }
        return null;
    }
}