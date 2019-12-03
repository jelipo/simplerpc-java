package com.jelipo.simplerpc.exception;

/**
 * 序列化 异常
 *
 * @author Jelipo
 * @date 2019/5/29 2:39
 */
public class SerializationException extends Exception {

    public SerializationException() {
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }

    public SerializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
