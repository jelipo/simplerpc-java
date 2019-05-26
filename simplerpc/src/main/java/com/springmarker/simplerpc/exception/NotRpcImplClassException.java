package com.springmarker.simplerpc.exception;

/**
 * @author: Springmarker
 * @date: 2019/5/27 3:23
 */
public class NotRpcImplClassException extends Exception {

    private String className;

    public NotRpcImplClassException(String message, Throwable cause, String className) {
        super(message, cause);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
