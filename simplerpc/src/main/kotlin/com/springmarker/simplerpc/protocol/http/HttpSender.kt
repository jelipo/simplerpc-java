package com.springmarker.simplerpc.protocol.http

import com.springmarker.simplerpc.core.client.SenderInterface
import com.springmarker.simplerpc.pojo.ExchangeRequest
import com.springmarker.simplerpc.protocol.http.pojo.HttpExchangeRequest

/**
 * @author Frank
 * @date 2018/10/21 23:00
 */
class HttpSender(
        val url: String
) : SenderInterface {


    init {

    }

    override fun send(request: ExchangeRequest): Any? {
        val httpRequest = request as HttpExchangeRequest
        return "send"
    }

}