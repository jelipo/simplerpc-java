package com.springmarker.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定义一个基本通讯Request对象。
 *
 * @author Springmarker
 * @date 2018/10/24 23:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequest {

    private int clientId;
    private int id;
    private RpcRequest rpcRequest;


}