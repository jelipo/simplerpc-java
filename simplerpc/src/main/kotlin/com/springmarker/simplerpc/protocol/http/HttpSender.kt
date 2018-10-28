package com.springmarker.simplerpc.protocol.http

import com.springmarker.simplerpc.core.client.SenderInterface
import java.lang.reflect.Method

/**
 * @author Frank
 * @date 2018/10/21 23:00
 */
class HttpSender(
        val url: String
) : SenderInterface {

    init {

    }

    override fun send(method: Method, args: Array<out Any>): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}