package com.springmarker.simplerpc.exception;

/**
 * Class重复异常
 * @author Frank
 * @date 2018/10/22 0:05
 */
public class DuplicateClassException extends Exception {
    public DuplicateClassException(String message) {
        super(message);
    }
}