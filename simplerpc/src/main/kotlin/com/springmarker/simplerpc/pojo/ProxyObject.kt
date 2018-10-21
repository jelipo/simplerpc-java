package com.springmarker.simplerpc.pojo

import java.lang.reflect.Method

/**
 * @author Frank
 * @date 2018/10/21 23:44
 */
data class ProxyObject(
        val fullName: String,
        val methods: List<ProxyMethod>,
        val anyObject: Any
) {
    data class ProxyMethod(
            val method: Method,
            val absoluteId: Int
    )
}