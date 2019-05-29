package com.springmarker.simplerpc.protocol.serialization.json;

import com.springmarker.simplerpc.pojo.RpcRequest;
import com.springmarker.simplerpc.pojo.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: Springmarker
 * @date: 2019/5/29 3:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponseJsonExchange extends RpcResponse {

    private Class resultClass;

    private String resultStr;
}
