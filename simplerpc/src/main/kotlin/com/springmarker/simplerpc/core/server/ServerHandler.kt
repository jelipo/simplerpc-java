package com.springmarker.simplerpc.core.server

import com.springmarker.simplerpc.pojo.ServerConfig

/**
 * @author Frank
 * @date 2018/10/28 22:40
 */
interface ServerHandler {

    fun start(config:ServerConfig,proxyServerCore: ProxyServerCore)

}