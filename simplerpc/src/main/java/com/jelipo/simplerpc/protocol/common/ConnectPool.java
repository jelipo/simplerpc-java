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
        CompletableFuture<BaseConnect<T>> connectCompletableFuture = connectStarter.creatConnect();
        connectCompletableFuture.whenComplete((baseConnect, throwable) -> {
            if (throwable == null) {
                innerConnectPool.addConnect(baseConnect);
            } else {
                // TODO 尝试重新连接
            }
        });
    }

    private void removeConnect(BaseConnect<T> baseConnect) {
        innerConnectPool.remove(baseConnect);
        baseConnect.destory();
    }


    /**
     * 获取随机的 {@link BaseConnect}
     */
    public BaseConnect<T> getRandomConnect() {
        return innerConnectPool.getRandomConnect();
    }


    /**
     * 内部维护的连接池
     */
    private class InnerConnectPool {

        private CopyOnWriteArrayList<BaseConnect<T>> baseConnectList = new CopyOnWriteArrayList<>();


        private BaseConnect<T> getRandomConnect() {
            int size = baseConnectList.size();
            if (size == 0) {
                return null;
            }
            return baseConnectList.get(random.nextInt(baseConnectList.size()));
        }

        private void addConnect(BaseConnect<T> baseConnect) {
            baseConnectList.add(baseConnect);
        }

        private void remove(BaseConnect<T> baseConnect) {
            baseConnectList.remove(baseConnect);
        }
    }


}
