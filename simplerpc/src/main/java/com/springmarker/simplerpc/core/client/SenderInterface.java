package com.springmarker.simplerpc.core.client;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

/**
 * 此接口用于定义如何发送消息，主要定义
 *
 * @author Frank
 * @date 2018/10/16 22:29
 */
public interface SenderInterface {

    /**
     * 此接口实现具体发送。
     * @param method
     * @param args
     * @return
     */
    Future<Object> send(Method method, Object[] args);


}