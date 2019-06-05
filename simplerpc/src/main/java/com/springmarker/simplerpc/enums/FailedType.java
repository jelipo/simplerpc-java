package com.springmarker.simplerpc.enums;

/**
 * @author: Springmarker
 * @date: 2019/6/2 1:39
 */
public enum FailedType {
    /**
     * RPC内部错误，可能与网络、序列化等有关。
     */
    RPC_EXCEPTION,

    /**
     * 远程调用方法，方法所报出的异常。
     */
    METHOD_EXCEPTION

}
