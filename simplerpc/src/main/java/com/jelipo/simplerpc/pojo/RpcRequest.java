package com.jelipo.simplerpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jelipo
 * @date 2019/5/21 15:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {

    private int methodHashCode;

    private ArrayList<Object> paramList;

    /**
     * 是否需要返回值。{@code 1} 需要，else {@code 0} 。
     */
    private int needReturn;

    /**
     * 是否使用异步方式。{@code 1} 异步方式，else {@code 0} 。
     */
    private int async;
}
