package com.jelipo.simplerpc.exception;

/**
 * 反序列化 异常
 *
 * @author Jelipo
 * @date 2019/5/29 2:39
 */
public class DeserializationException extends Exception {

    public DeserializationException() {
    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeserializationException(Throwable cause) {
        super(cause);
    }

    public DeserializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
