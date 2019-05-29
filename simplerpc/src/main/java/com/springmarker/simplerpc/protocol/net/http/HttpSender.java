package com.springmarker.simplerpc.protocol.net.http;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Springmarker
 * @date 2018/10/21 23:00
 */
public class HttpSender implements SenderInterface {

    private String url;
    private DataSerialization dataSerialization;

    private OkHttpClient okHttpClient = new OkHttpClient();

    public HttpSender(String url, DataSerialization dataSerialization) {
        this.url = url;
        this.dataSerialization = dataSerialization;
    }

    private LoadingCache<Integer, Method> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(key -> null);

    @Override
    public Object send(Method method, Object[] args) {
        Class<?> returnType = method.getReturnType();
        var needReturn = returnType.equals(Void.TYPE) ? 0 : 1;
        RpcRequest rpcRequest = new RpcRequest(method.hashCode(), List.of(args), needReturn);
        try {
            Request request = buildOkHttpRequest(rpcRequest);
            Response response = okHttpClient.newCall(request).execute();
            return deserializeResponse(response);

        } catch (SerializationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final MediaType mediaType = MediaType.parse("application/octet-stream");

    private Request buildOkHttpRequest(RpcRequest rpcRequest) throws SerializationException {
        byte[] serialize = dataSerialization.serialize(rpcRequest);
        RequestBody requestBody = RequestBody.create(mediaType, serialize);
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private Object deserializeResponse(Response response) {
        //TODO
        byte[] bytes = response.body().bytes();
        RpcResponse rpcResponse = dataSerialization.deserializeResponse(bytes);
        rpcResponse.getResult();
    }
}