package com.springmarker.simplerpc.protocol.http

import com.springmarker.simplerpc.core.client.SenderInterface
import com.springmarker.simplerpc.core.CallBack

/**
 * @author Frank
 * @date 2018/10/21 23:00
 */
class HttpSender : SenderInterface {

    override fun send(any: Any): Any? {
        return "send"
    }

    override fun sendAsyn(callBack: CallBack) {
        return
    }
}