package com.springmarker.simplerpc.exception;

import java.lang.Exception;

/**
 * @author Frank
 * @date 2018/10/22 0:24
 */
public class NotInterfaceException extends Exception {
    public NotInterfaceException(String message) {
        super(message);
    }
}