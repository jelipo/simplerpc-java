package com.jelipo.simplerpc.annotations;

import java.lang.annotation.*;

/**
 * 对想要支持RPC的Interface使用此注解。
 * 不支持内部类。
 *
 * @author Jelipo
 * @date 2018/11/23 12:21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Rpc {
}
