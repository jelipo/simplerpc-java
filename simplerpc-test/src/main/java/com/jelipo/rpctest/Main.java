package com.jelipo.rpctest;

import com.jelipo.simplerpc.RpcClient;
import com.jelipo.simplerpc.RpcServer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jelipo
 * @date 2019/12/20 0:49
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //设置端口
        int port = 18080;
        //启动RPC服务器
        RpcServer rpcServer = new RpcServer()
                .port(port)
                //扫描被注解的类的路径
                .classesPath("com.jelipo.rpctest")
                .start();

        //启动RPC客户端
        RpcClient rpcClient = new RpcClient()
                //连接RPC服务
                .hostAndPort("localhost", port)
                .classesPath("com.jelipo.rpctest")
                .connect();

        //从client中获取RPC接口的代理类。
        ProxyInterface proxyInterfaceImpl = rpcClient.getRpcImpl(ProxyInterface.class);

//

//        //同步调用RPC方法
//        People people = new People("小丽", 18);
//        //支持的参数和返回类型包括Java的基本类型、String、只包含基本类型(可嵌套)且有空构造方法的POJO类.
//        String result = proxyInterfaceImpl.getUserData(people);
//        System.out.println(result);

        final long l = System.currentTimeMillis();
        int num = 200000;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < num; i++) {
            //异步调用RPC方法
            CompletableFuture<People> completableFutureResult = proxyInterfaceImpl.getUserDataAsysn("老王,35");
            completableFutureResult.whenComplete((people1, throwable) -> {
                int i1 = atomicInteger.addAndGet(1);
                //System.out.println(Thread.currentThread().getName());
                if (i1 == num) {
                    System.out.println(System.currentTimeMillis() - l);
                }
            });
        }
//
//        //当异步调用远程方法，远程方法抛出异常时的处理。
//        CompletableFuture<People> exceptionResult = proxyInterfaceImpl.getUserDataAsysn("不知道老王几岁");
//        exceptionResult.whenComplete((people2, throwable) -> {
//            System.out.println(people2);
//        }).exceptionally(throwable -> {
//            throwable.printStackTrace();
//            return null;
//        });

    }
}