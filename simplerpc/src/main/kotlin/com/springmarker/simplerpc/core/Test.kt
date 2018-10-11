package com.springmarker.simplerpc.core

import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

/**
 * @author Springmarker
 * @date 2018/9/27 22:49
 */
class Test: MethodInterceptor {

    override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy?): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}