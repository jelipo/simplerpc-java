package com.jelipo.simplerpc.enums;

import com.jelipo.simplerpc.core.client.RpcSender;
import com.jelipo.simplerpc.protocol.net.socket.client.NettySender;

/**
 * @author Jelipo
 * @date 2018/10/21 22:44
 */
public enum NetProtocolType {

    /**
     * 默认
     */
    DEFAULT(NettySender.class),

    /**
     * Netty协议
     */
    NETTY_SOCKET(NettySender.class);

    NetProtocolType(Class<? extends RpcSender> senderImplClassPath) {
    }

    public Class<? extends RpcSender> get() {
        return this.get();
    }

}