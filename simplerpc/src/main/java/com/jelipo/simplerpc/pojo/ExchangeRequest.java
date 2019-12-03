package com.jelipo.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定义一个基本通讯Request对象。
 *
 * @author Jelipo
 * @date 2018/10/24 23:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequest {

    /**
     * 状态代码，如果为0则只代表心跳
     */
    private int status = 0;

    private int clientId;
    /**
     * 识别请求和回应的ID。
     */
    private int id;
    private RpcRequest rpcRequest;


}