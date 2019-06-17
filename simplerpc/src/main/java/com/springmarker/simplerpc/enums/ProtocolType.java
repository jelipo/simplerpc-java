package com.springmarker.simplerpc.enums;

import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.protocol.net.netty.NettySender;

/**
 * @author Springmarker
 * @date 2018/10/21 22:44
 */

public enum ProtocolType {
    /**
     * Netty协议
     */
    NETTY(NettySender.class);

    ProtocolType(Class<? extends SenderInterface> senderImplClassPath) {
    }
}