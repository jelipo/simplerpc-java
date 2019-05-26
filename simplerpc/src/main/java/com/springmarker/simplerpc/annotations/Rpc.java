package com.springmarker.simplerpc.annotations;

import java.lang.annotation.*;

/**
 * @author: Springmarker
 * @date: 2018/11/23 12:21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Rpc {
}
