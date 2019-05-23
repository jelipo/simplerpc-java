package com.springmarker.simplerpc.core.server;

import com.springmarker.simplerpc.exception.DuplicateClassException;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Springmarker
 * @date 2018/10/15 21:12
 */
public class RpcServerFactory {


    private List<Class<Object>> classList;

    /**
     * key：hashcode = class的名称+method 的名称。
     * value：根据Class生成的实体类。
     */
    private ConcurrentHashMap<String, Object> nameMap;

    public RpcServerFactory(List<Class<Object>> classList) {
        this.classList = classList;
        nameMap = new ConcurrentHashMap<>();
        for (Class<Object> objectClass : classList) {
            try {
                add(objectClass);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | DuplicateClassException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据 clazz 创建代理类，并添加到 [RpcClientFactory] 中，clazz 必须为一个接口类型。
     *
     * @param clazz 某个 [Class]
     */
    public void add(Class<Object> clazz) throws DuplicateClassException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        //检查是否添加过了
        if (checkDuplicateClass(clazz)) {
            throw new DuplicateClassException("The " + clazz.getCanonicalName() + " has been added.");
        }
        Object obj = clazz.getDeclaredConstructor().newInstance();
        //TODO 创建hashcode
        clazz.getInterfaces();
        nameMap.put(clazz.getCanonicalName(), obj);
    }


    /**
     * 检查nameMap 是否有相同的 class
     *
     * @return true:已经存在。false：还未添加过。
     */
    private boolean checkDuplicateClass(Class<Object> clazz) {
        //TODO 检查是否存在
        return false;
    }

}