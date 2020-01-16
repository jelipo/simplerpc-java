package com.jelipo.simplerpc.pojo;

import lombok.*;

/**
 * @author Jelipo
 * @date 2019/5/27 4:02
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {

    private Object result;

    private int exception = ExceptionType.NO_EXCEPTION;

}
