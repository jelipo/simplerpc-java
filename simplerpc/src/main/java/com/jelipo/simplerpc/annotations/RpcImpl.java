package com.jelipo.simplerpc.annotations;

import java.lang.annotation.*;

/**
 * 使用在RPC interface的实现类上的注解。
 * 不支持内部类。
 *
 * @author Jelipo
 * @date 2019/6/18 17:10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcImpl {
}
