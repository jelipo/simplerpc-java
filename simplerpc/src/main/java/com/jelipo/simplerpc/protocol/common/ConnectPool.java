package com.jelipo.simplerpc.protocol.common;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jelipo
 * @date 2019/12/26 21:43
 */
public class ConnectPool<T> {


    private static final Random random = new Random();

    private ConnectStarter<T> connectStarter;

    private int needConnectNum;

    private InnerConnectPool innerConnectPool;

    public ConnectPool(ConnectStarter<T> connectStarter, int needConnectNum) {
        this.connectStarter = connectStarter;
        this.needConnectNum = needConnectNum;
        this.innerConnectPool = new InnerConnectPool();
        init();
    }

    private void init() {
        for (int i = 0; i < needConnectNum; i++) {
            newConnect();
        }
    }

    public void newConnect() {
        CompletableFuture<Connect<T>> connectCompletableFuture = connectStarter.creatConnect();
        connectCompletableFuture.whenComplete((connect, throwable) -> {
            if (throwable == null) {
                innerConnectPool.addConnect(connect);
            } else {
                // TODO 尝试重新连接
            }
        });
    }

    private void removeConnect(Connect<T> connect) {
        innerConnectPool.remove(connect);
        connect.destory();
    }


    /**
     * 获取随机的 {@link Connect}
     */
    public Connect<T> getRandomConnect() {
        return innerConnectPool.getRandomConnect();
    }


    /**
     * 内部维护的连接池
     */
    private class InnerConnectPool {

        private CopyOnWriteArrayList<Connect<T>> connectList = new CopyOnWriteArrayList<>();


        private Connect<T> getRandomConnect() {
            int size = connectList.size();
            if (size == 0) {
                return null;
            }
            return connectList.get(random.nextInt(connectList.size()));
        }

        private void addConnect(Connect<T> connect) {
            connectList.add(connect);
        }

        private void remove(Connect<T> connect) {
            connectList.remove(connect);
        }
    }


}
