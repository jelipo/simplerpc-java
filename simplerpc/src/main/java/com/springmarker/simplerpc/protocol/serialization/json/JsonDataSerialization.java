package com.springmarker.simplerpc.protocol.serialization.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springmarker.simplerpc.exception.DeserializationException;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;

import java.util.List;

/**
 * @author: Springmarker
 * @date: 2019/5/28 19:22
 */
public class JsonDataSerialization implements DataSerialization {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonDataSerialization() {
        objectMapper = objectMapper.findAndRegisterModules();
        objectMapper = objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    @Override
    public byte[] serialize(RpcRequest request) throws SerializationException {
        RpcRequestJsonExchange requestWithExchange = change(request);
        try {
            return objectMapper.writeValueAsBytes(requestWithExchange);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Json serialization failed , message:" + e.getMessage(), e.getCause());
        }
    }

    @Override
    public byte[] serialize(RpcResponse response) throws SerializationException {
        return new byte[0];
    }

    @Override
    public RpcRequest deserializeRequest(byte[] bytes) throws DeserializationException {
        return null;
    }

    @Override
    public RpcResponse deserializeResponse(byte[] bytes) throws DeserializationException {
        return null;
    }

    private RpcRequestJsonExchange change(RpcRequest rpcRequest) {
        return null;
    }

    private RpcResponseJsonExchange change(RpcResponse rpcRequest) {
        return null;
    }
}
