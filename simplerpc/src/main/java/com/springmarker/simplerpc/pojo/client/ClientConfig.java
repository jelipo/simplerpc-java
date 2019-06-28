package com.springmarker.simplerpc.pojo.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定义基本的客户端信息。
 *
 * @author Springmarker
 * @date 2019/6/28 18:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientConfig {

    private String host;

    private int port;

    /**
     * 允许连接失败的最大重试次数。
     */
    private int retryTimes;

}
