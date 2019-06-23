package com.springmarker.simplerpc;

import com.springmarker.simplerpc.annotations.Rpc;
import com.springmarker.simplerpc.annotations.RpcImpl;
import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.core.server.Receiver;
import com.springmarker.simplerpc.core.server.RpcServerFactory;
import com.springmarker.simplerpc.pojo.ServerConfig;
import com.springmarker.simplerpc.protocol.net.netty.server.NettyServer;
import com.springmarker.simplerpc.protocol.serialization.kryo.KryoDataSerialization;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 启动Rpc服务端的主要配置和启动类。
 *
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

    /**
     * 将Rpc Server启动，本方法是同步方法，会等待启动完成。
     */
    public RpcServer start() throws Exception {
        RpcServerFactory rpcServerFactory = new RpcServerFactory(this.rpcInterfaceImplList, this.rpcInterfaceList);
        ProxyServerCore proxyServerCore = new ProxyServerCore(new Receiver(rpcServerFactory));
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(port);
        NettyServer httpServerHandler = new NettyServer(serverConfig, proxyServerCore, new KryoDataSerialization(10 * 1024));
        httpServerHandler.start();
        return this;
    }


    /**
     * 根据给定的class路径和注解类，扫描相应被注解的类。
     */
    private Set<Class<?>> findRpcClasses(String classesPath, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(classesPath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(annotation, true);
        return classSet.stream()
                //过滤掉内部类、匿名类、本地类
                .filter(aClass -> (!(aClass.isAnonymousClass() || aClass.isMemberClass() || aClass.isLocalClass())))
                .collect(Collectors.toSet());
    }

}
