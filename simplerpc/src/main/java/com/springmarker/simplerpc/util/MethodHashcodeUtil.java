package com.springmarker.simplerpc.util;

import java.lang.reflect.Method;

/**
 * @author Springmarker
 * @date 2019/6/18 2:50
 */
public class MethodHashcodeUtil {
    public static int methodHashcode(Method method) {
        int i = method.getDeclaringClass().getName().hashCode() ^ method.getName().hashCode();
        StringBuilder stringBuilder = new StringBuilder();
        for (Class<?> parameterType : method.getParameterTypes()) {
            stringBuilder.append(parameterType.getName());
        }
        return i ^ stringBuilder.toString().hashCode();
    }
}
