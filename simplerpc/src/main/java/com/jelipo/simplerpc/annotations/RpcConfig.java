package com.jelipo.simplerpc.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 配置于 method 级别的注解，用于配置超时等设置
 * @author chn
 * @date 2020/2/18 18:49
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcConfig {

    int timeout() default 10;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
