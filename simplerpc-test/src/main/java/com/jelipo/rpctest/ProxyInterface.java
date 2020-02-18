package com.jelipo.rpctest;

import com.jelipo.simplerpc.annotations.Rpc;
import com.jelipo.simplerpc.annotations.RpcConfig;

import java.util.concurrent.CompletableFuture;

/**
 * @author Jelipo
 * @date 2019/12/20 0:46
 */
@Rpc //在RPC接口上添加@Rpc注解
public interface ProxyInterface {

    @RpcConfig(timeout = 1)
    String getUserData(People people);

    CompletableFuture<People> getUserDataAsysn(String info);
}
