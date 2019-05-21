package com.springmarker.simplerpc.pojo;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Frank
 * @date 2018/10/21 23:44
 */
public class ProxyObject {
    /**
     * class的完整名称。
     */
    String fullName;
    /**
     * 接口method的List。
     */
    List<ProxyMethod> methods;
    /**
     * 创建出来的代理对象。
     */
    Object anyObject;

    public class ProxyMethod {
        boolean asyn;
        Method method;
        int hashCod;
    }
}