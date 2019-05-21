package com.springmarker.simplerpc.enums;

import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.protocol.http.HttpSender;

/**
 * @author Frank
 * @date 2018/10/21 22:44
 */
public enum ProtocolType {
    /**
     * HTTP协议
     */
    HTTP(HttpSender.class);

    private Class<? extends SenderInterface> senderImplClassPath;

    ProtocolType(Class<? extends SenderInterface> senderImplClassPath) {
        this.senderImplClassPath = senderImplClassPath;
    }
}