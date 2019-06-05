package com.springmarker.simplerpc.protocol.serialization.json;

import com.springmarker.simplerpc.pojo.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author: Springmarker
 * @date: 2019/5/29 3:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RpcResponseJsonExchange extends RpcResponse {

    private Class resultClass;

    private String resultStr;
}
