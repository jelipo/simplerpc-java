package com.jelipo.simplerpc.protocol.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 基于Guava的 {@link Cache}实现的RpcId缓存
 *
 * @author Jelipo
 * @date 2020/3/17 14:19
 */
public class GuavaIdFutureCache implements IdFutureCache {

    /**
     * 用于存放 释放锁对象 的缓存。
     */
    private Cache<Integer, CompletableFuture<Object>> cache;

    /**
     * @param cacheTime 最长缓存的时间(秒)
     */
    public GuavaIdFutureCache(int cacheTime) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(Integer.MAX_VALUE)
                .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
                .build();
    }


    @Override
    public void putCache(int rpcId, CompletableFuture<Object> completableFuture) {
        cache.put(rpcId, completableFuture);
    }

    @Override
    public CompletableFuture<Object> get(int rpcId) {
        return cache.getIfPresent(rpcId);
    }

    @Override
    public CompletableFuture<Object> getAndDelete(int rpcId) {
        CompletableFuture<Object> future = cache.getIfPresent(rpcId);
        delete(rpcId);
        return future;
    }

    @Override
    public void delete(int rpcId) {
        cache.invalidate(rpcId);
    }

}
