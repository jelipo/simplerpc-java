package com.jelipo.simplerpc.core.server;

import com.jelipo.simplerpc.pojo.ProtocolMeta;
import com.jelipo.simplerpc.pojo.RpcRequest;
import com.jelipo.simplerpc.pojo.RpcResponse;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


/**
 * @author Jelipo
 * @date 2018/10/15 21:23
 */
public class ProxyServerCore {

    private Receiver receiver;

    public ProxyServerCore(Receiver receiver) {
        this.receiver = receiver;
    }

    public RpcResponse handleMethod(RpcRequest rpcRequest, ProtocolMeta protocolHeader) {
        return receiver.receive(rpcRequest.getMethodHashCode(), rpcRequest.getParamList());
    }

    public void handleAsyncMethod(RpcRequest rpcRequest, CompletableFuture future, ProtocolMeta protocolHeader) {
        int methodHashCode = rpcRequest.getMethodHashCode();
        ArrayList<Object> paramList = rpcRequest.getParamList();
        receiver.receiveAsync(methodHashCode, paramList, future);
    }

}