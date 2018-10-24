package com.springmarker.simplerpc.pojo

/**
 * 定义一个基本通讯Request对象。
 *
 * @author Frank
 * @date 2018/10/24 23:02
 */
interface BaseExcangeRequest {
    val clientId: Int
    val id: Int
    val methodHashCode: Int
    val paramList: List<Any>
    val needReturn: Int
}