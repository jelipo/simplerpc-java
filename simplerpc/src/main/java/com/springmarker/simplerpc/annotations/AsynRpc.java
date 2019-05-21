package com.springmarker.simplerpc.annotations;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AsynRpc {
}
