package com.springmarker.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 *
 * @author: Springmarker
 * @date: 2019/5/21 15:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    private int methodHashCode;
    private List<Object> paramList;
    private int needReturn;
}
