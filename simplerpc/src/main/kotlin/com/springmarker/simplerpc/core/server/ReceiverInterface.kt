package com.springmarker.simplerpc.core.server

/**
 * 此接口用于定义如何接收消息，主要定义
 *
 * @author Frank
 * @date 2018/10/16 22:29
 */
interface ReceiverInterface {

    fun receive(byteArray: Array<Byte>): Any?

}