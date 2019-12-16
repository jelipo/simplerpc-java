package com.jelipo.simplerpc.core.server;

import com.jelipo.simplerpc.exception.DuplicateClassException;
import com.jelipo.simplerpc.exception.ImplClassAdditionFailedException;
import com.jelipo.simplerpc.exception.NotRpcImplClassException;
import com.jelipo.simplerpc.util.MethodHashcodeUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Jelipo
 * @date 2018/10/15 21:12
 */
public class RpcServerFactory {

    /**
     * Rpc接口实现类的实现工厂。
     */
    private ClassCteatorFactory classCteatorFactory;

    public RpcServerFactory(Collection<Class> implClassList, Collection<Class> interfaceClassList) {
        classCteatorFactory = new ClassCteatorFactory(interfaceClassList);
        for (Class objectClass : implClassList) {
            try {
                add(objectClass);
            } catch (ImplClassAdditionFailedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据Rpc接口的某个方法获取对应实现类的实现方法。
     *
     * @param method Rpc接口的某个方法
     * @return Rpc接口实现类的某个方法。
     */
    public Method getImplMethodByInterfaceMethod(Method method) {
        return classCteatorFactory.getImplMethodByInterfaceMethod(MethodHashcodeUtil.methodHashcode(method));
    }

    /**
     * 根据Rpc接口获取实现类的对象。
     *
     * @param clazz Rpc接口类。
     * @return Rpc接口实现类的实体对象。
     */
    public Object getImplObjectByInterfaceClass(Class clazz) {
        return classCteatorFactory.getImplObjectByInterfaceClass(clazz);
    }

    /**
     * 根据Rpc接口的某个方法的hashcode获取对应实现类的实现方法。
     *
     * @param hashCode Rpc接口的某个方法的hashcode。
     * @return Rpc接口实现类的某个方法。
     */
    public Method getImplMethodByInterfaceMethodHashcode(int hashCode) {
        return classCteatorFactory.getImplMethodByInterfaceMethod(hashCode);
    }

    /**
     * 根据给定的Class创建代理类，并添加到 [RpcClientFactory] 中，Class必须为一个RPC接口类型。
     * 不是线程安全。
     *
     * @param clazz 实现RPC接口的实现类。
     * @throws ImplClassAdditionFailedException Rpc接口的实现类添加异常。不会影响程序的正常运行。
     */
    public void add(Class<Object> clazz) throws ImplClassAdditionFailedException {

        try {
            if (!classCteatorFactory.add(clazz)) {
                throw new DuplicateClassException("The impl class \""
                        + clazz.getCanonicalName() + "\" has been added.");
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                InstantiationException | NotRpcImplClassException | DuplicateClassException e) {
            e.printStackTrace();
            throw new ImplClassAdditionFailedException(e.getMessage(), e.getCause(), clazz.getCanonicalName());
        }
    }


    /**
     * Rpc接口实现类的实现工厂。
     */
    private class ClassCteatorFactory {

        /**
         * value ： RPC接口类。
         */
        private HashSet<Class> interfaceClassSet;
        /**
         * key:实现RPC接口的是实现类。 value:实现类生成的对象。
         */
        private HashMap<Class, Object> objectMapByImplClass = new HashMap<>();
        /**
         * key:RPC接口。 value: 实现RPC接口的类
         */
        private HashMap<Class, Class> implClassMapByInterfaceClass = new HashMap<>();
        /**
         * key:RPC接口的某个方法的HashCode。 value:RPC接口实现类的某个方法。
         */
        private HashMap<Long, Method> implMethodMapByInterfaceMethodHashcode = new HashMap<>();

        ClassCteatorFactory(Collection<Class> interfaceClass) {
            this.interfaceClassSet = new HashSet<>(interfaceClass);
        }

        /**
         * 根据Interface定义的方法获取实现类中
         *
         * @param hashcode RPC接口的某个方法的HashCode
         * @return 对应 RPC接口实现类某个方法 的某个方法
         */
        Method getImplMethodByInterfaceMethod(long hashcode) {
            return implMethodMapByInterfaceMethodHashcode.get(hashcode);
        }

        Object getImplObjectByInterfaceClass(Class clazz) {
            return objectMapByImplClass.get(clazz);
        }

        /**
         * 根据Class对象添加。
         *
         * @param implClazz 实现RPC接口的Class。
         * @return 是否成功。如果遇到创建类对象的异常则会直接抛出异常。
         */
        boolean add(Class implClazz) throws NoSuchMethodException, IllegalAccessException,
                InvocationTargetException, InstantiationException, NotRpcImplClassException {
            if (objectMapByImplClass.containsKey(implClazz)) {
                return false;
            }

            for (Class<?> interfaceClass : implClazz.getInterfaces()) {
                //判断是否实现了RPC接口。
                if (!interfaceClassSet.contains(interfaceClass)) {
                    continue;
                }
                //判断RPC接口是否存在 已经存储了的实现类。
                if (implClassMapByInterfaceClass.containsKey(interfaceClass)) {
                    return false;
                }
                newRpcClass(implClazz, interfaceClass);
                return true;
            }
            throw new NotRpcImplClassException("The class \"\"", null, implClazz.getCanonicalName());
        }

        /**
         * 创建RPC实现类的方法。
         */
        private void newRpcClass(Class implClass, Class interfaceClass) throws NoSuchMethodException,
                IllegalAccessException, InvocationTargetException, InstantiationException {
            Object obj = implClass.getDeclaredConstructor().newInstance();
            objectMapByImplClass.put(implClass, obj);
            implClassMapByInterfaceClass.put(interfaceClass, implClass);
            for (Method interfaceMethod : interfaceClass.getMethods()) {
                Method implClassMethod = implClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
                implMethodMapByInterfaceMethodHashcode.put(MethodHashcodeUtil.methodHashcode(interfaceMethod), implClassMethod);
            }
        }
    }

}