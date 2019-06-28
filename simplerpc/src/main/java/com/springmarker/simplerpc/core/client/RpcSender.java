package com.springmarker.simplerpc.core.client;

import com.springmarker.simplerpc.pojo.RpcRequest;

import java.util.concurrent.CompletableFuture;

/**
 * 此接口用于定义如何发送消息，主要定义
 *
 * @author Springmarker
 * @date 2018/10/16 22:29
 */
public interface RpcSender {

    /**
     * 实现具体发送,并同步返回结果。
     *
     * @param rpcRequest
     * @return RPC调用完成后返回的执行结果。
     * @throws Exception 序列化/发送 可能出现的异常。
     */
    Object syncSend(RpcRequest rpcRequest) throws Exception;


    /**
     * 实现具体的异步发送,并返回CompletableFuture。
     *
     * @param rpcRequest
     * @return RPC信息发送后返回的 {@link CompletableFuture}。
     * @throws Exception 序列化/发送 可能出现的异常。
     */
    CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) throws Exception;

    /**
     * 关闭连接
     */
    void close();


}