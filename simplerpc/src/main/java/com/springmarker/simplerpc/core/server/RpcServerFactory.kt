package com.springmarker.simplerpc.core.server

import com.springmarker.simplerpc.core.client.ProxyClientCore
import com.springmarker.simplerpc.core.client.RpcClientFactory
import com.springmarker.simplerpc.core.client.SenderInterface
import com.springmarker.simplerpc.exception.DuplicateClassException
import net.sf.cglib.proxy.Enhancer
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Springmarker
 * @date 2018/10/15 21:12
 */
class RpcServerFactory(sender: SenderInterface, classList: List<Class<*>>) {

    private val nameMap: ConcurrentHashMap<String, Any?> = ConcurrentHashMap()

    private var proxyCore: ProxyClientCore = ProxyClientCore(sender)

    init {
        classList.forEach { add(it) }
    }

    /**
     * 根据clazz获取代理对象。
     *
     * @param clazz 某个接口的 [Class]
     * @return clazz实体代理对象
     */
    fun <T> get(clazz: Class<T>): T? {
        val any = nameMap[clazz.name] ?: return null
        return any as T
    }

    /**
     * 根据 clazz 创建代理类，并添加到 [RpcClientFactory] 中，clazz 必须为一个接口类型。
     *
     * @param clazz 某个 [Class]
     */
    fun add(clazz: Class<*>) {
        //检查是否添加过了
        if (checkDuplicateClass(clazz)) {
            throw DuplicateClassException("The ${clazz.canonicalName} has been added.")
        }
        val proxy = creatProxy(clazz)
        nameMap[clazz.canonicalName] = proxy

    }


    /**
     * 实际使用cglib创建代理的方法。
     *
     * @return 创建好的代理实体。
     */
    private fun creatProxy(clazz: Class<*>): Any {
        val enhancer = Enhancer()
        enhancer.setSuperclass(clazz)
        enhancer.setCallback(proxyCore)
        return enhancer.create()
    }

    /**
     * 检查nameMap 是否有相同的 class
     *
     * @return true:已经存在。false：还未添加过。
     */
    private fun checkDuplicateClass(clazz: Class<*>): Boolean {
        return nameMap.containsKey(clazz.canonicalName)
    }

}