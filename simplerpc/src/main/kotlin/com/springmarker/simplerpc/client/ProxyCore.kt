package com.springmarker.simplerpc.client

import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method


/**
 * @author Springmarker
 * @date 2018/10/15 21:23
 */
internal class ProxyCore : MethodInterceptor {

    override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy?): Any {
        TODO("完成接口的具体实现")
    }


}