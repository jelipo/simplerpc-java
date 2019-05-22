package com.springmarker.simplerpc.protocol.http;

import com.springmarker.simplerpc.core.client.SenderInterface;

import java.lang.reflect.Method;

/**
 * @author Frank
 * @date 2018/10/21 23:00
 */
public class HttpSender implements SenderInterface {
    private String url;

    public HttpSender(String url) {
        this.url = url;
    }

    @Override
    public Object send(Method method, Object[] args) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null;
    }
}