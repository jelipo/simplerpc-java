package com.springmarker.simplerpc.protocol.net.http;

import com.springmarker.simplerpc.core.client.SenderInterface;
import com.springmarker.simplerpc.core.client.SyncCallback;
import com.springmarker.simplerpc.enums.FailedType;
import com.springmarker.simplerpc.exception.DeserializationException;
import com.springmarker.simplerpc.exception.SerializationException;
import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Springmarker
 * @date 2018/10/21 23:00
 */
public class HttpSender implements SenderInterface {

    private String url;

    /**
     * 用于反序列化的处理器。
     */
    private DataSerialization dataSerialization;

    /**
     * 此Sender类默认的OkHttp客户端。
     */
    private OkHttpClient okHttpClient = new OkHttpClient();

    public HttpSender(String url, DataSerialization dataSerialization) {
        this.url = url;
        this.dataSerialization = dataSerialization;
    }


    private static final MediaType mediaType = MediaType.parse("application/octet-stream");

    /**
     * 根据RpcRequest构建OkHTTP的Request。
     *
     * @param rpcRequest
     * @return
     * @throws SerializationException
     */
    private Request buildOkHttpRequest(RpcRequest rpcRequest) throws SerializationException {
        byte[] serialize = dataSerialization.serialize(rpcRequest);
        RequestBody requestBody = RequestBody.create(mediaType, serialize);
        return new Request.Builder().url(url).post(requestBody).build();
    }

    @Override
    public Object syncSend(RpcRequest rpcRequest) {
        CompletableFuture<Object> future = send(rpcRequest);
        return future.join();
    }

    @Override
    public CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) {
        return send(rpcRequest);
    }

    private CompletableFuture<Object> send(RpcRequest rpcRequest) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        Request request;
        try {
            request = buildOkHttpRequest(rpcRequest);
        } catch (SerializationException e) {
            future.completeExceptionally(e);
            return future;
        }
        Call call = okHttpClient.newCall(request);

        SyncCallback syncCallback = new SyncCallback() {
            @Override
            public void onSuccess(Object object) {
                future.complete(object);
            }

            @Override
            public void onFailed(Throwable throwable, FailedType type) {
                future.completeExceptionally(throwable);
            }
        };
        call.enqueue(new OkHttpSyncCallback(syncCallback));
        return future;
    }


    private class OkHttpSyncCallback implements Callback {

        private SyncCallback syncCallback;

        OkHttpSyncCallback(SyncCallback syncCallback) {
            this.syncCallback = syncCallback;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            syncCallback.onFailed(e, FailedType.RPC_EXCEPTION);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                Object result = deserializeResponse(response);
                syncCallback.onSuccess(result);
            } catch (DeserializationException e) {
                syncCallback.onFailed(e, FailedType.RPC_EXCEPTION);
            }
        }

        /**
         * 将OkHTTP的Response中的返回结果反序列化。
         *
         * @param response
         * @return
         */
        private Object deserializeResponse(Response response) throws DeserializationException, IOException {
            //TODO
            byte[] bytes = response.body().bytes();
            RpcResponse rpcResponse = dataSerialization.deserializeResponse(bytes);
            rpcResponse.getResult();
            return null;
        }
    }
}