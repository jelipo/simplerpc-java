package com.springmarker.simplerpc.protocol.netty.pojo

import com.springmarker.simplerpc.pojo.ServerConfig

/**
 * @author Frank
 * @date 2018/10/28 22:56
 */
data class NettyServerConfig(
        override val port: Int
) : ServerConfig