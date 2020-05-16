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

        //String host = "192.168.100.95";

        String host = "localhost";
        //设置端口
        int port = 18080;
        Main main = new Main();


        main.startServer(port);
        //Thread.sleep(99999999);

        Thread.sleep(2000);
        main.testRpc(host, port, main);


    }


    private void startServer(int port) throws Exception {
        //启动RPC服务器
        RpcServer rpcServer = new RpcServer()
                .port(port)
                //扫描被注解的类的路径
                .classesPath("com.jelipo.rpctest")
                .start();
    }

    private void testRpc(String host, int port, Main main) throws Exception {
        //启动RPC客户端
        RpcClient rpcClient = new RpcClient()
                //连接RPC服务
                .hostAndPort(host, port)
                .classesPath("com.jelipo.rpctest")
                .connect();

        //从client中获取RPC接口的代理类。
        ProxyInterface proxyInterfaceImpl = rpcClient.getRpcImpl(ProxyInterface.class);

        new Thread(() -> {
            main.conn(proxyInterfaceImpl);
        }).start();

//        new Thread(() -> {
//            main.conn(proxyInterfaceImpl);
//        }).start();
//
//        new Thread(() -> {
//            main.conn(proxyInterfaceImpl);
//        }).start();

//        new Thread(() -> {
//            main.conn(proxyInterfaceImpl);
//        }).start();
//
//        new Thread(() -> {
//            main.conn(proxyInterfaceImpl);
//        }).start();
//
//        new Thread(() -> {
//            main.conn(proxyInterfaceImpl);
//        }).start();

    }

    private void conn(ProxyInterface proxyInterfaceImpl) {

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
    }
}