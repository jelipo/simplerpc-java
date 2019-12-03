package com.jelipo.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jelipo
 * @date 2019/5/27 4:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {

    private Object result;

    private int exception = ExceptionType.NO_EXCEPTION;
}
