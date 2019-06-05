package com.springmarker.simplerpc.core.client;

import com.springmarker.simplerpc.exception.DuplicateClassException;
import com.springmarker.simplerpc.exception.NotInterfaceException;
import net.sf.cglib.proxy.Enhancer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Springmarker
 * @date 2018/10/15 21:12
 */
public class RpcClientFactory {

    private SenderInterface sender;
    private List<Class<Object>> classList;

    private ProxyClientCore proxyCore;
    /**
     * key:class的名称。 value
     */
    private ConcurrentHashMap<String, Object> nameMap = new ConcurrentHashMap<>();

    public RpcClientFactory(SenderInterface sender, List<Class<Object>> classList) {
        this.sender = sender;
        this.classList = classList;
        proxyCore = new ProxyClientCore(sender);
        for (Class<Object> objectClass : classList) {
            try {
                add(objectClass);
            } catch (DuplicateClassException | NotInterfaceException ignored) {
                System.out.println("");
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
        var any = nameMap.get(clazz.getName());
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
        var proxy = creatProxy(clazz);
        nameMap.put(clazz.getCanonicalName(), proxy);
    }


    /**
     * 实际使用cglib创建代理的方法。
     *
     * @return 创建好的代理实体。
     */
    private Object creatProxy(Class<Object> clazz) {
        var enhancer = new Enhancer();
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