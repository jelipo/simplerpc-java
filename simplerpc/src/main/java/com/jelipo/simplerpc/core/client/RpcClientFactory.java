package com.jelipo.simplerpc.core.client;

import com.jelipo.simplerpc.exception.DuplicateClassException;
import com.jelipo.simplerpc.exception.NotInterfaceException;
import net.sf.cglib.proxy.Enhancer;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jelipo
 * @date 2018/10/15 21:12
 */
public class RpcClientFactory {

    private RpcSender sender;
    private Collection<Class> classList;

    private ProxyClientCore proxyCore;
    /**
     * key:class的名称。 value
     */
    private ConcurrentHashMap<String, Object> nameMap = new ConcurrentHashMap<>();

    public RpcClientFactory(RpcSender sender, Collection<Class> classList) {
        this.sender = sender;
        this.classList = classList;
        this.proxyCore = new ProxyClientCore(sender);
        for (Class objectClass : classList) {
            try {
                add(objectClass);
            } catch (DuplicateClassException | NotInterfaceException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据clazz获取代理对象。
     *
     * @param clazz 某个接口的 [Class]
     * @return clazz实体代理对象
     */
    public <T> T get(Class<T> clazz) {
        Object any = nameMap.get(clazz.getName());
        return (T) any;
    }

    /**
     * 根据 clazz 创建代理类，并添加到 [RpcClientFactory] 中，clazz 必须为一个接口类型。
     *
     * @param clazz 某个Interface的类文件
     */
    public void add(Class<Object> clazz) throws DuplicateClassException, NotInterfaceException {
        //检查是否添加过了
        if (checkDuplicateClass(clazz)) {
            throw new DuplicateClassException("The " + clazz.getCanonicalName() + " has been added.");
        }
        //检查是否是接口类
        if (!clazz.isInterface()) {
            throw new NotInterfaceException("${clazz.canonicalName} is not an interface.");
        }
        Object proxy = creatProxy(clazz);
        nameMap.put(clazz.getCanonicalName(), proxy);
    }


    /**
     * 实际使用cglib创建代理的方法。
     *
     * @return 创建好的代理实体。
     */
    private Object creatProxy(Class<Object> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(proxyCore);
        return enhancer.create();
    }

    /**
     * 检查nameMap 是否有相同的 class
     *
     * @return true:已经存在。false：还未添加过。
     */
    private boolean checkDuplicateClass(Class<Object> clazz) {
        return nameMap.containsKey(clazz.getCanonicalName());
    }


}