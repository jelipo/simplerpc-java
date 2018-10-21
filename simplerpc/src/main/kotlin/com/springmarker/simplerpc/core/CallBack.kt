package com.springmarker.simplerpc.core

/**
 * @author Frank
 * @date 2018/10/17 22:50
 */
interface CallBack {

    fun callback(bytes: Array<Byte>)

}