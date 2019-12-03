package com.jelipo.simplerpc.protocol.serialization;

import com.jelipo.simplerpc.exception.DeserializationException;
import com.jelipo.simplerpc.pojo.ExchangeRequest;
import com.jelipo.simplerpc.pojo.ExchangeResponse;
import com.jelipo.simplerpc.exception.SerializationException;

import java.io.InputStream;

/**
 * 序列化/反序列化 处理 接口。
 *
 * @author Jelipo
 * @date 2019/5/24 3:14
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
     * @param inputStream 输入流
     * @return
     */
    ExchangeRequest deserializeRequest(InputStream inputStream) throws DeserializationException;

    /**
     * 反序列化
     *
     * @param inputStream 输入流
     * @return
     */
    ExchangeResponse deserializeResponse(InputStream inputStream) throws DeserializationException;

}
