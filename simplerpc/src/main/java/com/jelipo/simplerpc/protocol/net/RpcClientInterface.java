package com.jelipo.simplerpc.protocol.net;

import com.jelipo.simplerpc.core.client.RpcSender;

/**
 * @author Jelipo
 * @date 2019/6/28 19:08
 */
public interface RpcClientInterface {

    /**
     * 获取NettySender
     */
    RpcSender getNettySender();
}
