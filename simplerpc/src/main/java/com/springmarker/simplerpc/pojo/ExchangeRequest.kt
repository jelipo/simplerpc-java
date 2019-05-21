package com.springmarker.simplerpc.pojo

/**
 * 定义一个基本通讯Request对象。
 *
 * @author Frank
 * @date 2018/10/24 23:02
 */
class ExchangeRequest(
        val clientId: Int,
        val id: Int,
        val rpcRequest: RpcRequest
) {

    class RpcRequest(
            val methodHashCode: Int,
            val paramList: List<Any>,
            val needReturn: Int
    )

}