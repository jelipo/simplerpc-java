package com.jelipo.simplerpc.protocol.net.socket.client;

import com.google.common.cache.Cache;
import com.jelipo.simplerpc.pojo.client.ClientConfig;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * @author Jelipo
 * @date 2019/6/28 18:54
 */
@AllArgsConstructor
@Getter
public class NettyClientContext {

    private DataSerialization dataSerialization;

    private ClientConfig clientConfig;

    private Cache<Integer, CompletableFuture<Object>> cache;

}
