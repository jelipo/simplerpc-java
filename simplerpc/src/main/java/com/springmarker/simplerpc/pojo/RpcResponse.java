package com.springmarker.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: Springmarker
 * @date: 2019/5/27 4:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private Object result;
    private int exception = 0;
}
