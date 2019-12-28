package com.jelipo.simplerpc.protocol.common;

/**
 * @author Jelipo
 * @date 2019/12/26 22:08
 */
public abstract class BaseConnect<T> {

    private T nativeConnect;

    public BaseConnect(T nativeConnect) {
        this.nativeConnect = nativeConnect;
    }

    public abstract void destory();

}
