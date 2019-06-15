package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


/**
 * @author Springmarker
 * @date 2018/10/15 21:23
 */
public class ProxyServerCore {

    private Receiver receiver;

    public ProxyServerCore(Receiver receiver) {
        this.receiver = receiver;
    }

    public RpcResponse handleMethod(RpcRequest rpcRequest) {
        ArrayList<Object> paramList = rpcRequest.getParamList();
        return receiver.receive(rpcRequest.getMethodHashCode(), rpcRequest.getParamList());
    }

    public void handleAsyncMethod(RpcRequest rpcRequest, CompletableFuture future) {
        int methodHashCode = rpcRequest.getMethodHashCode();
        ArrayList<Object> paramList = rpcRequest.getParamList();
        receiver.receiveAsync(methodHashCode, paramList, future);
    }

}