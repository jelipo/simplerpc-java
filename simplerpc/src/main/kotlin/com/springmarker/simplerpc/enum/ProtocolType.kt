package com.springmarker.simplerpc.enum

import com.springmarker.simplerpc.client.SenderInterface
import com.springmarker.simplerpc.protocol.http.HttpSender

/**
 * @author Frank
 * @date 2018/10/21 22:44
 */
enum class ProtocolType(var senderImplClassPath: Class<out SenderInterface>) {
    HTTP(HttpSender::class.java)
}