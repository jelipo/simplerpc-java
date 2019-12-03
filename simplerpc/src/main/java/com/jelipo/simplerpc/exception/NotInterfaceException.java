package com.jelipo.simplerpc.exception;

import java.lang.Exception;

/**
 * @author Jelipo
 * @date 2018/10/22 0:24
 */
public class NotInterfaceException extends Exception {
    public NotInterfaceException(String message) {
        super(message);
    }
}