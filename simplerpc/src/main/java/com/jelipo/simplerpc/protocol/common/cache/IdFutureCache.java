package com.jelipo.simplerpc.protocol.common.cache;

import java.util.concurrent.CompletableFuture;

/**
 * 提供了一个通用的 CompletableFuture 缓存
 * 一般用于通讯时上下行唯一ID的识别
 *
 * @author Jelipo
 * @date 2020/3/17 15:29
 */
public interface IdFutureCache {

    /**
     * 添加缓存
     *
     * @param rpcId             RpcId
     * @param completableFuture 需要缓存的Future
     */
    void putCache(int rpcId, CompletableFuture<Object> completableFuture);

    /**
     * 根据指定RpcId获取相应的缓存的future
     *
     * @param rpcId RpcId
     * @return 被缓存的Future
     */
    CompletableFuture<Object> get(int rpcId);

    /**
     * 根据指定RpcId获取相应的缓存的future，然后删除缓存
     *
     * @param rpcId RpcId
     * @return 被缓存的Future
     */
    CompletableFuture<Object> getAndDelete(int rpcId);

    /**
     * 根据指定RpcId删除缓存
     *
     * @param rpcId RpcId
     */
    void delete(int rpcId);

}
