package com.jelipo.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jelipo
 * @date 2019/6/17 21:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeResponse {
    /**
     * 识别请求和回应的ID。
     * 正常id都应为正数，如果为负数，说明此ID不用处理。
     */
    private int id;

    private RpcResponse rpcResponse;
}
