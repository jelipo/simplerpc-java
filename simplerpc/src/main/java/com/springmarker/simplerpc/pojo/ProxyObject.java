package com.springmarker.simplerpc.pojo;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Springmarker
 * @date 2018/10/21 23:44
 */
@Data
public class ProxyObject {
    /**
     * class的完整名称。
     */
    private String fullName;
    /**
     * 接口method的List。
     */
    private List<ProxyMethod> methods;
    /**
     * 创建出来的代理对象。
     */
    private Object anyObject;

    public class ProxyMethod {
        boolean asyn;
        Method method;
        int hashCod;
    }
}