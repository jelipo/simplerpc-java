package com.springmarker.simplerpc.core.client;

import com.springmarker.simplerpc.pojo.RpcRequest;

import java.util.concurrent.CompletableFuture;

/**
 * 此接口用于定义如何发送消息，主要定义
 *
 * @author Springmarker
 * @date 2018/10/16 22:29
 */
public interface SenderInterface {

    /**
     * 实现具体发送,并同步返回结果。
     *
     * @param rpcRequest
     * @return
     */
    Object syncSend(RpcRequest rpcRequest);


    /**
     * 实现具体的异步发送,并返回CompletableFuture。
     *
     * @param rpcRequest
     * @return
     */
    CompletableFuture<Object> asyncSend(RpcRequest rpcRequest);


}