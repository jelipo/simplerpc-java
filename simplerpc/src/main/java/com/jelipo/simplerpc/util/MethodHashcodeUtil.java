package com.jelipo.simplerpc.util;

import java.lang.reflect.Method;

/**
 * @author Jelipo
 * @date 2019/6/18 2:50
 */
public class MethodHashcodeUtil {

    /**
     * 获取方法的唯一 Hascode
     *
     * @param method Java的 Method 实体类
     * @return hashcode
     */
    public static int methodHashcode(Method method) {
        int i = method.getDeclaringClass().getName().hashCode() ^ method.getName().hashCode();
        StringBuilder stringBuilder = new StringBuilder();
        for (Class<?> parameterType : method.getParameterTypes()) {
            stringBuilder.append(parameterType.getName());
        }
        return i ^ stringBuilder.toString().hashCode();
    }

}
