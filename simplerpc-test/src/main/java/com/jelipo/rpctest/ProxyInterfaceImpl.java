package com.jelipo.rpctest;

import com.jelipo.simplerpc.annotations.RpcImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jelipo
 * @date 2019/12/20 0:48
 */
@RpcImpl //在实现类上添加@RpcImpl注解
public class ProxyInterfaceImpl implements ProxyInterface {

    @Override
    public String getUserData(People people) {
        return "Sync: Name:" + people.getName() + ". Age:" + people.getAge();
    }

    AtomicInteger atomicInteger = new AtomicInteger(0);

    private long l = System.currentTimeMillis();

    @Override
    public CompletableFuture<People> getUserDataAsysn(String info) {
        return CompletableFuture.supplyAsync(() -> {
            String[] split = info.split(",");
            return new People(split[0], Integer.parseInt(split[1]), new byte[102400]);
        });
    }

}
