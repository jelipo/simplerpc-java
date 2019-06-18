package com.springmarker.simplerpc;

import com.springmarker.simplerpc.annotations.Rpc;
import com.springmarker.simplerpc.core.client.RpcClientFactory;
import com.springmarker.simplerpc.protocol.net.netty.NettySender;
import com.springmarker.simplerpc.protocol.serialization.kryo.KryoDataSerialization;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Springmarker
 * @date 2019/6/15 20:30
 */
public class RpcClient {

    private String host;

    private int port;

    private Set<Class> rpcInterfaceList = new HashSet<>();

    private RpcClientFactory rpcClientFactory;

    public RpcClient hostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
        return this;
    }

    public RpcClient classesPath(String... paths) {
        for (String path : paths) {
            Set<Class<?>> rpcClasses = findRpcClasses(path, Rpc.class);
            rpcInterfaceList.addAll(rpcClasses);
        }
        return this;
    }

    public RpcClient connect() throws InterruptedException {
        NettySender nettySender = null;
        nettySender = new NettySender("localhost", port, new KryoDataSerialization(10 * 1024));
        this.rpcClientFactory = new RpcClientFactory(nettySender, rpcInterfaceList);
        return this;
    }

    public <T> T getRpcImpl(Class<T> clazz) {
        return rpcClientFactory.get(clazz);
    }

    private Set<Class<?>> findRpcClasses(String classesPath, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(classesPath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(annotation, true);
        return classSet.stream()
                //过滤掉内部类、匿名类、本地类
                .filter(aClass -> (!(aClass.isAnonymousClass() || aClass.isMemberClass() || aClass.isLocalClass())))
                .collect(Collectors.toSet());
    }
}
