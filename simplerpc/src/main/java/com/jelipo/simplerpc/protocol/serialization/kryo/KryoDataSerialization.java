package com.jelipo.simplerpc.protocol.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.jelipo.simplerpc.exception.DeserializationException;
import com.jelipo.simplerpc.pojo.ExchangeRequest;
import com.jelipo.simplerpc.pojo.ExchangeResponse;
import com.jelipo.simplerpc.pojo.RpcRequest;
import com.jelipo.simplerpc.pojo.RpcResponse;
import com.jelipo.simplerpc.exception.SerializationException;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * 使用Kryo作为序列化和反序列化的主要类。
 *
 * @author Jelipo
 * @date 2019/6/10 20:34
 */
public class KryoDataSerialization implements DataSerialization {

    private static final int OUTPUT_BUFFER_SIZE = 384;

    /**
     * 将最大数量控制在可用核心数的2倍。
     */
    private Pool<Kryo> kryoPool = new Pool<Kryo>(true, false,
            Runtime.getRuntime().availableProcessors() * 2) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setReferences(false);
            //提前注册会用到的类。
            kryo.register(HashSet.class);
            kryo.register(HashMap.class);
            kryo.register(LinkedList.class);
            kryo.register(ArrayList.class);
            //以下是内部必须要用到的。
            kryo.register(RpcRequest.class);
            kryo.register(ExchangeRequest.class);
            kryo.register(RpcResponse.class);
            kryo.register(ExchangeResponse.class);
            return kryo;
        }
    };

    private Pool<Output> outputPool;

    /**
     * 因为Kroy会用到buffer缓存，为了重复使用,请设置一个合理的buffer最大值。
     * 此Buffer会常驻内存，且为多个，一帮低负载情况下，buffer的总大小会小于等于 CPU逻辑核心数*2*buffersize，
     * 但是到高负载情况下，会高于此值。
     *
     * @param maxBufferSize
     */
    public KryoDataSerialization(int maxBufferSize) {
        this.outputPool = new Pool<Output>(true, false) {
            @Override
            protected Output create() {
                return new Output(OUTPUT_BUFFER_SIZE, maxBufferSize);
            }
        };
    }

    @Override
    public byte[] serialize(ExchangeRequest exchangeRequest) throws SerializationException {
        return commonSerialize(exchangeRequest);
    }

    @Override
    public byte[] serialize(ExchangeResponse exchangeResponse) throws SerializationException {
        return commonSerialize(exchangeResponse);
    }

    /**
     * 内部通用的序列化方法。
     *
     * @param object
     * @return
     * @throws SerializationException
     */
    private byte[] commonSerialize(Object object) throws SerializationException {
        Kryo kryo = kryoPool.obtain();
        Output output = outputPool.obtain();
        try {
            kryo.writeObject(output, object);
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializationException("Kryo serialize failed." + e.getMessage(), e.getCause());
        } finally {
            kryoPool.free(kryo);
            outputPool.free(output);
        }
    }


    @Override
    public ExchangeRequest deserializeRequest(InputStream inputStream) throws DeserializationException {
        Input input = new Input(inputStream);
        return (ExchangeRequest) commonDeserialize(input, ExchangeRequest.class);
    }

    @Override
    public ExchangeResponse deserializeResponse(InputStream inputStream) throws DeserializationException {
        Input input = new Input(inputStream);
        return (ExchangeResponse) commonDeserialize(input, ExchangeResponse.class);
    }


    /**
     * 内部通用的反序列化方法。
     *
     * @param input 需要反序列化的input。
     */
    private Object commonDeserialize(Input input, Class clazz) throws DeserializationException {
        Kryo kryo = kryoPool.obtain();
        try {
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            throw new DeserializationException("Kryo deserialize failed." + e.getMessage(), e.getCause());
        } finally {
            kryoPool.free(kryo);
            input.close();
        }
    }
}