package com.springmarker.simplerpc.exception;

/**
 * Rpc接口的实现类添加失败的异常。
 *
 * @author: Springmarker
 * @date: 2019/5/27 3:30
 */
public class ImplClassAdditionFailedException extends Exception {

    private String className;

    public ImplClassAdditionFailedException(String message, Throwable cause, String className) {
        super(message, cause);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
