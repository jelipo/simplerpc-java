package com.jelipo.simplerpc.protocol.common;

import com.jelipo.simplerpc.exception.RemoteCallException;
import com.jelipo.simplerpc.pojo.ExceptionType;

/**
 * 异常过滤器
 * 以异常Code作为参数，判断是否有异常
 *
 * @author Jelipo
 * @date 2020/3/17 14:39
 */
public class RpcExceptionFilter {

    /**
     * 是否发生了异常
     */
    public static boolean isException(int exceptionTypeCode) {
        return exceptionTypeCode != ExceptionType.NO_EXCEPTION;
    }

    /**
     * 主方法，根据code判断是否有异常，然后根据code返回相应的Exception
     *
     * @param exceptionCode RPC通信中的异常Code，基于{@link ExceptionType}
     */
    public static Exception filter(int exceptionCode) {
        switch (exceptionCode) {
            case ExceptionType.NO_EXCEPTION:
                return null;
            case ExceptionType.RPC_METHOD_EXCEPTION:
                return new RemoteCallException("An exception occurred when calling a remote method.");
            case ExceptionType.RPC_INNER_EXCEPTION:
                return new RemoteCallException("Abnormality in RPC.Pelease check network / object size / supported serialized objects ");
            case ExceptionType.NO_SUCHMETHOD_EXCEPTION:
                return new NoSuchMethodException("No such method in remote server.");
            case ExceptionType.SERIALIZED_EXCEPTION:
                return new RemoteCallException("Abnormality in RPC. Serialized exception.");
            case ExceptionType.NETWORK_TRANSMISSION_EXCEPTION:
                return new RemoteCallException("Abnormality in RPC. Exception occurred during netty transmission.");
            default:
                return new Exception("Unknow remote call exception code: " + exceptionCode);
        }
    }


}
