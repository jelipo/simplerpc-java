package com.springmarker.simplerpc.protocol.net.netty;

import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.pojo.RpcRequest;

import java.util.concurrent.CompletableFuture;

/**
 * @author: Springmarker
 * @date: 2019/6/11 2:51
 */
public class NettySender implements SenderInterface {
    @Override
    public Object syncSend(RpcRequest rpcRequest) {
        return null;
    }

    @Override
    public CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) {
        return null;
    }
}
