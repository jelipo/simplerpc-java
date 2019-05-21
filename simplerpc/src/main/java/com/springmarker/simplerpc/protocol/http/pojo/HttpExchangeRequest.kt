package com.springmarker.simplerpc.protocol.http.pojo

import com.springmarker.simplerpc.pojo.ExchangeRequest

/**
 * @author Frank
 * @date 2018/10/24 23:46
 */
data class HttpExchangeRequest(
        val type: String, 
        override val clientId: Int, 
        override val id: Int, 
        override val methodHashCode: Int, 
        override val paramList: List<Any>, 
        override val needReturn: Int
) 