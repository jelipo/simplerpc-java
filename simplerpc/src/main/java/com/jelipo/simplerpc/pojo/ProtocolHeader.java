package com.jelipo.simplerpc.pojo;

import com.google.common.primitives.Ints;

/**
 * 关于协议头的处理类。
 * 与网络协议和序列化协议无任何关系，只是用于RPC通讯过程中需要的参数，
 * 可以认为是RPC通讯过程中的一个META 或者 header，用于识别通讯类型、客户端ID、RPC请求识别ID 等。
 * 为了方便扩展，head头不会限制数组总长度。
 * <pre>
 * |============================================================================================
 * |       index     | 说明
 * |-----------------|--------------------------------------------------------------------------
 * |        [0]      | RPC传输的类型，1字节。 -1:心跳包
 * |    [1,2,3,4]    | 客户端ID值，4字节。int类型
 * |    [5,6,7,8]    | RPC请求识别ID，4字节
 * |============================================================================================
 * </pre>
 *
 * @author Jelipo
 * @date 2019/12/13 0:05
 */
public class ProtocolHeader {

    public ProtocolHeader(byte[] headerBytes) {
        this.bytes = headerBytes;
    }

    private byte[] bytes;

    /**
     * 是否是心跳包
     * 一般无协议的长连接会需要此方法来判断是否是心跳包，方便进行处理。
     */
    public boolean isHeatBeat() {
        return bytes[1] == -1;
    }

    public int getClientId() {
        return Ints.fromBytes(bytes[1], bytes[2], bytes[3], bytes[4]);
    }

    public int getRpcId() {
        return Ints.fromBytes(bytes[5], bytes[6], bytes[7], bytes[8]);
    }

    public static byte[] creatHeader(boolean isHeatBeat, int clientId, int rpcId) {
        byte[] newHeaderBytes = new byte[9];
        newHeaderBytes[0] = isHeatBeat ? (byte) -1 : (byte) 0;
        byte[] clientIdBytes = Ints.toByteArray(clientId);
        System.arraycopy(clientIdBytes, 0, newHeaderBytes, 1, clientIdBytes.length);
        byte[] rpcIdBytes = Ints.toByteArray(rpcId);
        System.arraycopy(rpcIdBytes, 0, newHeaderBytes, 5, rpcIdBytes.length);
        return newHeaderBytes;
    }
}
