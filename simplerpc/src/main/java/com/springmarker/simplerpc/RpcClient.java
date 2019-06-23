package com.springmarker.simplerpc;

import com.springmarker.simplerpc.annotations.Rpc;
import com.springmarker.simplerpc.core.client.RpcClientFactory;
import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.protocol.net.netty.client.NettySender;
import com.springmarker.simplerpc.protocol.serialization.kryo.KryoDataSerialization;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 启动Rpc客户端的主要启动类。
 *
 * @author Springmarker
 * @date 2019/6/15 20:30
 */
public class RpcClient {

    private String host;

    private int port;

    private Set<Class> rpcInterfaceList = new HashSet<>();

    private RpcClientFactory rpcClientFactory;

    private SenderInterface sender;

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

    /**
     * 连接Rpc Server，同步操作，会等待完成连接完成。
     *
     * @return
     * @throws InterruptedException
     */
    public RpcClient connect() throws InterruptedException {
        SenderInterface sender = new NettySender(this.host, this.port, new KryoDataSerialization(10 * 1024));
        this.rpcClientFactory = new RpcClientFactory(sender, rpcInterfaceList);
        this.sender = sender;
        return this;
    }

    /**
     * 根据给定的Rpc接口类找到由Rpc代理的对象，找不到时返回null。
     * 需要执行 connect 方法成功后才可调用。
     *
     * @param clazz Rpc的接口。
     * @return 由Rpc代理的对象。
     */
    public <T> T getRpcImpl(Class<T> clazz) {
        return rpcClientFactory.get(clazz);
    }

    /**
     * 关闭连接
     */
    public void close() {
        sender.close();
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
