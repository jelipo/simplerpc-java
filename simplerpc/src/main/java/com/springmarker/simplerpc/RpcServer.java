package com.springmarker.simplerpc;

import com.springmarker.simplerpc.annotations.Rpc;
import com.springmarker.simplerpc.annotations.RpcImpl;
import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.core.server.Receiver;
import com.springmarker.simplerpc.core.server.RpcServerFactory;
import com.springmarker.simplerpc.pojo.ServerConfig;
import com.springmarker.simplerpc.protocol.net.netty.NettyServer;
import com.springmarker.simplerpc.protocol.serialization.kryo.KryoDataSerialization;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Springmarker
 * @date 2019/6/18 20:31
 */
public class RpcServer {

    private int port;

    private Set<Class> rpcInterfaceList = new HashSet<>();

    private Set<Class> rpcInterfaceImplList = new HashSet<>();

    public RpcServer port(int port) {
        this.port = port;
        return this;
    }

    public RpcServer classesPath(String... paths) {
        for (String path : paths) {
            Set<Class<?>> rpcClasses = findRpcClasses(path, Rpc.class);
            rpcInterfaceList.addAll(rpcClasses);
            Set<Class<?>> rpcImplClasses = findRpcClasses(path, RpcImpl.class);
            rpcInterfaceImplList.addAll(rpcImplClasses);
        }
        return this;
    }

    public RpcServer start() {
        RpcServerFactory rpcServerFactory = new RpcServerFactory(this.rpcInterfaceImplList, this.rpcInterfaceList);
        ProxyServerCore proxyServerCore = new ProxyServerCore(new Receiver(rpcServerFactory));
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(port);
        NettyServer httpServerHandler = new NettyServer(serverConfig, proxyServerCore, new KryoDataSerialization(10 * 1024));
        try {
            httpServerHandler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private Set<Class<?>> findRpcClasses(String classesPath, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(classesPath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(annotation,true);
        return classSet.stream()
                //过滤掉内部类、匿名类、本地类
                .filter(aClass -> (!(aClass.isAnonymousClass() || aClass.isMemberClass() || aClass.isLocalClass())))
                .collect(Collectors.toSet());
    }

}
