package com.jelipo.simplerpc.protocol.net;

import com.jelipo.simplerpc.pojo.ProtocolMeta;

import java.util.Map;

/**
 * 关于协议头的处理类。
 * 与网络协议和序列化协议无任何关系，只是用于RPC通讯过程中需要的参数，
 * 可以认为是RPC通讯过程中的一个META，用于识别通讯类型、客户端ID、RPC请求识别ID 等。
 * 为了方便扩展，head头不会限制数组总长度。
 * <pre>
 * |============================================================================================
 * |       index     | 说明
 * |-----------------|--------------------------------------------------------------------------
 * |        [0]      | RPC传输的类型，1字节。 0:心跳包
 * |    [1,2,3,4]    | RPC请求识别ID，4字节
 * |      [5,..]     | 客户端ID，字符串类型，UTF-8编码
 * |============================================================================================
 * </pre>
 *
 * @author Jelipo
 * @date 2019/12/13 0:05
 */
public class CommonMetaUtils {

    public static byte[] toBytes(boolean isHeatBeat, int rpcId, String clientId, Map<String, String> customParams) {
        StringBuilder metaBuilder = new StringBuilder();
        metaBuilder.append('h').append(':').append(isHeatBeat ? '0' : '1').append("\n");
        metaBuilder.append('r').append(':').append(rpcId).append("\n");
        metaBuilder.append('c').append(':').append(clientId).append("\n");
        if (customParams != null) {
            customParams.forEach((key, value) -> metaBuilder.append(key).append(':').append(value).append("\n"));
        }
        return metaBuilder.toString().getBytes();
    }

    public static ProtocolMeta deserialize(byte[] bytes) {
        ProtocolMeta protocolMeta = new ProtocolMeta();
        int lastIndex = 0;
        byte singleByte = 0;
        byte[] keyBytes = null;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b == ':') {
                int keyLength = i - lastIndex;
                if (keyLength == 1) {
                    singleByte = bytes[i - 1];
                } else {
                    keyBytes = new byte[keyLength];
                    System.arraycopy(bytes, lastIndex, keyBytes, 0, keyLength);
                }
                lastIndex = i + 1;
            } else if (b == '\n') {
                int valueLength = i - lastIndex;
                String value = new String(bytes, lastIndex, valueLength);
                if (keyBytes == null) {
                    pack(singleByte, value, protocolMeta);
                } else {
                    protocolMeta.getCustomParams().put(new String(keyBytes), value);
                }
                keyBytes = null;
                lastIndex = i + 1;
            }
        }
        return protocolMeta;
    }

    private static void pack(byte key, String value, ProtocolMeta protocolMeta) {
        switch (key) {
            case 'h': {
                protocolMeta.setHreatBeat(value.charAt(0) == '0');
                return;
            }
            case 'r': {
                protocolMeta.setRpcId(Integer.parseInt(value));
                return;
            }
            case 'c': {
                protocolMeta.setClientId(value);
                return;
            }
            default: {
                protocolMeta.getCustomParams().put(Character.toString((char) key), value);
            }
        }
    }
}

