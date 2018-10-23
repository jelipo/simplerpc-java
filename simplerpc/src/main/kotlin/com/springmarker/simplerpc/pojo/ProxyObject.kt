package com.springmarker.simplerpc.pojo

import java.lang.reflect.Method

/**
 * @author Frank
 * @date 2018/10/21 23:44
 */
data class ProxyObject(
        /**
         * class的完整名称。
         */
        val fullName: String,
        /**
         * 接口method的List。
         */
        val methods: List<ProxyMethod>,
        /**
         * 创建出来的代理对象。
         */
        val anyObject: Any
) {
    data class ProxyMethod(
            val asyn: Boolean = false,
            val method: Method,
            val hashCode: Int
    )
}