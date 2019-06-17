package com.springmarker.simplerpc.protocol.serialization;

import com.springmarker.simplerpc.exception.DeserializationException;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.ExchangeResponse;
import com.springmarker.simplerpc.pojo.RpcResponse;

/**
 * 序列化/反序列化 处理 接口。
 *
 * @author: Springmarker
 * @date: 2019/5/24 3:14
 */
public interface DataSerialization {

    /**
     * 序列化
     *
     * @param exchangeRequest 要序列化的对象
     * @return
     */
    byte[] serialize(ExchangeRequest exchangeRequest) throws SerializationException;

    /**
     * 序列化
     *
     * @param exchangeResponse 要序列化的对象
     * @return
     */
    byte[] serialize(ExchangeResponse exchangeResponse) throws SerializationException;


    /**
     * 反序列化
     *
     * @param bytes 反序列化的字节数组
     * @return
     */
    ExchangeRequest deserializeRequest(byte[] bytes) throws DeserializationException;

    /**
     * 反序列化
     *
     * @param bytes 反序列化的字节数组
     * @return
     */
    ExchangeResponse deserializeResponse(byte[] bytes) throws DeserializationException;

}
