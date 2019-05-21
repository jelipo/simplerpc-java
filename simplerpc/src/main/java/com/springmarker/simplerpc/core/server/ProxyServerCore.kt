package com.springmarker.simplerpc.core.server

import com.springmarker.simplerpc.pojo.ExchangeRequest
import com.springmarker.simplerpc.pojo.ServerConfig
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method


/**
 * @author Springmarker
 * @date 2018/10/15 21:23
 */
class ProxyServerCore(
        private val receiver: ReceiverInterface,
        private val serverHandler: ServerHandler,
        private val serverConfig: ServerConfig
) {

    init {
        serverHandler.start(serverConfig, this)
    }

    fun handleMethod(baseExchangeRequest: ExchangeRequest): Any? {
        return receiver.receive(baseExchangeRequest.rpcRequest)
    }

    /**
     * 处理同步方法
     */
    private fun handleSyncRequest(obj: Any?, method: Method, args: Array<out Any>, proxy: MethodProxy?): Any? {
        this.receiver.receive(null)
        return null
    }

}