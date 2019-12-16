package com.jelipo.simplerpc.util;

import java.lang.reflect.Method;

/**
 * @author Jelipo
 * @date 2019/6/18 2:50
 */
public class MethodHashcodeUtil {

    /**
     * 获取方法的唯一 Hashcode
     *
     * @param method Java的 Method 实体类
     * @return hashcode
     */
    public static long methodHashcode(Method method) {
        int methodNameHashCode = method.getName().hashCode();
        StringBuilder stringBuilder = new StringBuilder();

        for (Class<?> parameterType : method.getParameterTypes()) {
            stringBuilder.append(parameterType.getName());
        }
        int methodHashCode = methodNameHashCode ^ stringBuilder.toString().hashCode();
        int classHashCode = method.getDeclaringClass().getName().hashCode();
        return classHashCode * 10000000000L + methodHashCode;
    }

}
