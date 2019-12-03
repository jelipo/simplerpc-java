package com.jelipo.simplerpc.pojo;

/**
 * RPC之中出现的异常的代号。
 *
 * @author Jelipo
 * @date 2019/6/18 16:08
 */
public class ExceptionType {

    /**
     * 0代表无异常。
     */
    public static final int NO_EXCEPTION = 0;

    /**
     * 代理调用的方法有异常。
     */
    public static final int RPC_METHOD_EXCEPTION = 1;

    /**
     * 代理调用正常，但是RPC在 内部处理 过程中出现了异常，此为比较笼统的一个异常，不能明确确定是什么异常。
     */
    public static final int RPC_INNER_EXCEPTION = 2;

    /**
     * 没有找到相应的方法。
     */
    public static final int NO_SUCHMETHOD_EXCEPTION = 3;

    /**
     * 将对象序列化/反序列化期间出现了异常。
     */
    public static final int SERIALIZED_EXCEPTION = 4;

    /**
     * 网络传输过程中出现了异常。
     */
    public static final int NETWORK_TRANSMISSION_EXCEPTION = 5;
}
