package com.jelipo.simplerpc.enums;

import com.jelipo.simplerpc.core.client.RpcSender;
import com.jelipo.simplerpc.protocol.net.socket.client.NettySender;

/**
 * @author Jelipo
 * @date 2018/10/21 22:44
 */

public enum ProtocolType {
    /**
     * Netty协议
     */
    NETTY(NettySender.class);

    ProtocolType(Class<? extends RpcSender> senderImplClassPath) {
    }
}