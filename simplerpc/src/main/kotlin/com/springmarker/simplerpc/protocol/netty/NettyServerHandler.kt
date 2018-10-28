package com.springmarker.simplerpc.protocol.netty

import com.springmarker.simplerpc.core.server.ProxyServerCore
import com.springmarker.simplerpc.core.server.ServerHandler
import com.springmarker.simplerpc.pojo.ExchangeRequest
import com.springmarker.simplerpc.pojo.ServerConfig
import com.springmarker.simplerpc.protocol.netty.pojo.NettyServerConfig

/**
 * @author Frank
 * @date 2018/10/28 22:58
 */
class NettyServerHandler : ServerHandler {

    private lateinit var proxyServerCore: ProxyServerCore

    override fun start(config: ServerConfig, proxyServerCore: ProxyServerCore) {
        val nettyConf = config as NettyServerConfig
        this.proxyServerCore = proxyServerCore
    }

    private fun handle() {
        val request = ExchangeRequest(0, 0,
                ExchangeRequest.RpcRequest(0, ArrayList(), 1)
        )
        proxyServerCore.handleMethod(request)
    }


}