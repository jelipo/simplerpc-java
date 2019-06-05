package com.springmarker.simplerpc.enums;

import com.springmarker.simplerpc.core.client.SenderInterface;

import com.springmarker.simplerpc.protocol.net.http.HttpSender;

/**
 * @author Springmarker
 * @date 2018/10/21 22:44
 */

public enum ProtocolType {
    /**
     * HTTP协议
     */
    HTTP(HttpSender.class);

    ProtocolType(Class<? extends SenderInterface> senderImplClassPath) {
    }
}