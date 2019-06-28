package com.springmarker.simplerpc.protocol.serialization;

import com.springmarker.simplerpc.exception.DeserializationException;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.ExchangeResponse;

import java.io.InputStream;

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
