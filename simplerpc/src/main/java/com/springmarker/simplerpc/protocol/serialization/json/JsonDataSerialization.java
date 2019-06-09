package com.springmarker.simplerpc.protocol.serialization.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springmarker.simplerpc.core.server.RpcServerFactory;
import com.springmarker.simplerpc.exception.DeserializationException;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Springmarker
 * @date: 2019/5/28 19:22
 */
public class JsonDataSerialization implements DataSerialization {

    private ObjectMapper objectMapper = new ObjectMapper();

    private RpcServerFactory rpcServerFactory;

    public JsonDataSerialization() {
        objectMapper = objectMapper.findAndRegisterModules();
        objectMapper = objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    @Override
    public void setRpcServerFactory(RpcServerFactory rpcServerFactory) {
        this.rpcServerFactory = rpcServerFactory;
    }

    @Override
    public byte[] serialize(ExchangeRequest exchangeRequest) throws SerializationException {
        try {
            RpcRequestJsonExchange requestWithExchange = change(exchangeRequest.getRpcRequest());
            return objectMapper.writeValueAsBytes(requestWithExchange);
        } catch (IOException e) {
            throw new SerializationException("Json serialization failed , message:" + e.getMessage(), e.getCause());
        }
    }

    @Override
    public byte[] serialize(RpcResponse response) throws SerializationException {
        try {
            RpcResponseJsonExchange responseWithExchange = change(response);
            return objectMapper.writeValueAsBytes(responseWithExchange);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Json serialization failed , message:" + e.getMessage(), e.getCause());
        }
    }


    @Override
    public ExchangeRequest deserializeRequest(byte[] bytes) throws DeserializationException {
        return null;
//        try {
//            RpcRequestJsonExchange requestJsonExchange = objectMapper.readValue(bytes, RpcRequestJsonExchange.class);
//            List<String> exchangeParams = requestJsonExchange.getExchangeStrParams();
//            if (exchangeParams != null) {
//                List<Object> paramList = new ArrayList<>(exchangeParams.size());
//                Class<?>[] parameterTypes = rpcServerFactory
//                        .getImplMethodByInterfaceMethodHashcode(requestJsonExchange.hashCode())
//                        .getParameterTypes();
//                for (int i = 0; i < exchangeParams.size(); i++) {
//                    Class clazz = parameterTypes[i];
//                    Object paramObj = changeType(clazz, exchangeParams.get(i));
//                    paramList.add(paramObj);
//                }
//                requestJsonExchange.setParamList(paramList);
//            }
//            return requestJsonExchange;
//        } catch (IOException e) {
//            throw new DeserializationException("Json serialization failed , message:" + e.getMessage(), e.getCause());
//        }
    }

    @Override
    public RpcResponse deserializeResponse(byte[] bytes) throws DeserializationException {
        try {
            RpcResponseJsonExchange responseExchange = objectMapper.readValue(bytes, RpcResponseJsonExchange.class);
            String resultStr = responseExchange.getResultStr();
            if (resultStr != null) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RpcRequestJsonExchange change(RpcRequest rpcRequest) throws IOException {
        List<Object> paramList = rpcRequest.getParamList();
        List<String> exchangeList = null;
        if (paramList != null) {
            exchangeList = new ArrayList<>(paramList.size());
            for (Object obj : paramList) {
                String paramStr = changeType(obj);
                exchangeList.add(paramStr);
            }
        }
        RpcRequestJsonExchange rpcRequestJsonExchange = new RpcRequestJsonExchange();
        rpcRequestJsonExchange.setExchangeStrParams(exchangeList);
        rpcRequestJsonExchange.setMethodHashCode(rpcRequest.getMethodHashCode());
        rpcRequestJsonExchange.setNeedReturn(rpcRequest.getNeedReturn());
        return rpcRequestJsonExchange;
    }

    private RpcResponseJsonExchange change(RpcResponse rpcResponse) throws JsonProcessingException {
        Object result = rpcResponse.getResult();
        RpcResponseJsonExchange rpcResponseJsonExchange = new RpcResponseJsonExchange();

        if (result == null) {
            rpcResponseJsonExchange.setResultStr(null);
        } else {
            String resultStr = objectMapper.writeValueAsString(result);
            rpcResponseJsonExchange.setResultStr(resultStr);
        }

        rpcResponseJsonExchange.setResultClass(result.getClass());
        rpcResponseJsonExchange.setException(rpcResponse.getException());
        return rpcResponseJsonExchange;
    }

    private Object changeType(Class clazz, String str) throws IOException {
        String canonicalName = clazz.getCanonicalName();
        switch (canonicalName) {
            case "java.lang.Integer":
                return Integer.valueOf(str);
            case "java.lang.Long":
                return Long.valueOf(str);
            case "java.lang.Float":
                return Float.valueOf(str);
            case "java.lang.Double":
                return Double.valueOf(str);
            case "java.lang.Byte":
                return Byte.valueOf(str);
            case "java.lang.Character":
                return str.getBytes()[0];
            case "java.lang.String":
                return str;
            default:
                return objectMapper.readValue(str, clazz);
        }
    }

    private String changeType(Object object) throws IOException {
        String canonicalName = object.getClass().getCanonicalName();
        switch (canonicalName) {
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Float":
            case "java.lang.Double":
            case "java.lang.Byte":
            case "java.lang.Character":
                return object.toString();
            case "java.lang.String":
                return (String) object;
            default:
                return objectMapper.writeValueAsString(object);
        }
    }

}
