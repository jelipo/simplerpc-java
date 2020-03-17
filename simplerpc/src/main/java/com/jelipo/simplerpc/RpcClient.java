package com.jelipo.simplerpc;


import com.jelipo.simplerpc.annotations.Rpc;
import com.jelipo.simplerpc.core.client.RpcClientFactory;
import com.jelipo.simplerpc.core.client.RpcInterfaceManager;
import com.jelipo.simplerpc.core.client.RpcSender;
import com.jelipo.simplerpc.protocol.net.RpcClientInterface;
import com.jelipo.simplerpc.protocol.net.socket.client.NettyClient;
import com.jelipo.simplerpc.protocol.net.socket.client.NettyClientConfig;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import com.jelipo.simplerpc.protocol.serialization.kryo.KryoDataSerialization;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 启动Rpc客户端的主要启动类。
 *
 * @author Jelipo
 * @date 2019/6/15 20:30
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private String host;

    private int port;

    private RpcInterfaceManager rpcInterfaceManager = new RpcInterfaceManager();

    private RpcClientFactory rpcClientFactory;

    private RpcSender sender;

    public RpcClient hostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
        return this;
    }

    public RpcClient classesPath(String... paths) {
        for (String path : paths) {
            Set<Class<?>> rpcClasses = findRpcClasses(path, Rpc.class);
            rpcClasses.forEach(aClass -> rpcInterfaceManager.add(aClass));
        }
        return this;
    }

    /**
     * 连接Rpc Server，同步操作，会等待完成连接完成。
     */
    public RpcClient connect() throws Exception {
        DataSerialization dataSerialization = creatDataSerialization();
        RpcClientInterface rpcClient = creatRpcClient(dataSerialization);
        RpcSender sender = rpcClient.getNettySender();
        this.rpcClientFactory = new RpcClientFactory(sender, rpcInterfaceManager);
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

    private DataSerialization creatDataSerialization() {
        return new KryoDataSerialization(10 * 1024);
    }

    private RpcClientInterface creatRpcClient(DataSerialization dataSerialization) throws Exception {
        NettyClientConfig config = new NettyClientConfig();
        config.setHost(host);
        config.setPort(port);
        config.setLengthFieldLength(3);
        config.setNettyMaxFrameLength(1024 * 1024);
        config.setRetryTimes(5);
        return new NettyClient(config, dataSerialization);
    }


}
