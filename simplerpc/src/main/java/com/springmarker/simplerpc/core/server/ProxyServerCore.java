package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.pojo.ServerConfig;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author Springmarker
 * @date 2018/10/15 21:23
 */
public class ProxyServerCore {

    private Receiver receiver;

    public ProxyServerCore(Receiver receiver) {
        this.receiver = receiver;
    }

    public RpcResponse handleMethod(ExchangeRequest baseExchangeRequest) {
        ArrayList<String> objects = new ArrayList<>();
        List<Integer> collect = objects.stream().map(Integer::valueOf).collect(Collectors.toUnmodifiableList());
        return receiver.receive(baseExchangeRequest.getRpcRequest());
    }

}