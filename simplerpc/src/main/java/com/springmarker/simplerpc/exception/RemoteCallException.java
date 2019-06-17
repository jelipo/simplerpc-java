package com.springmarker.simplerpc.exception;

/**
 * 调用远程RPC方法的时候出现的异常。
 *
 * @author Springmarker
 * @date 2019/6/17 22:11
 */
public class RemoteCallException extends Exception {
    public RemoteCallException() {
    }

    public RemoteCallException(String message) {
        super(message);
    }

    public RemoteCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteCallException(Throwable cause) {
        super(cause);
    }

    public RemoteCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
