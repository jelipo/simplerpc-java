package com.springmarker.simplerpc.protocol.net.netty.client;

import com.google.common.cache.Cache;
import com.springmarker.simplerpc.pojo.client.ClientConfig;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * @author Springmarker
 * @date 2019/6/28 18:54
 */
@AllArgsConstructor
@Getter
public class NettyClientContext {

    private DataSerialization dataSerialization;

    private ClientConfig clientConfig;

    private Cache<Integer, CompletableFuture<Object>> cache;

}
