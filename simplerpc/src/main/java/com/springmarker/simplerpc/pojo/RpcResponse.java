package com.springmarker.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Springmarker
 * @date: 2019/5/27 4:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {

    private Object result;
    /**
     * 是否有异常.
     * 0代表无异常。
     * 1代表 代理调用的方法有异常。
     * 2代表 代理调用正常，但是RPC在 传递/序列化 过程中出现了异常。
     */
    private int exception = 0;
}
