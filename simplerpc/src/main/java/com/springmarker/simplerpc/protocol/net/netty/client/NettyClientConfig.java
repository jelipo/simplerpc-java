package com.springmarker.simplerpc.protocol.net.netty.client;

import com.springmarker.simplerpc.pojo.client.ClientConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Springmarker
 * @date 2019/6/28 18:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyClientConfig extends ClientConfig {

    /**
     * Netty单次发送/接收 最大的字节数。
     */
    private int nettyMaxFrameLength = 1024 * 1024;

    /**
     * 用于Netty通讯包的 头部长度。
     * 只允许数值为 1, 2, 3, 4, 8 。
     */
    private int lengthFieldLength = 4;


}
