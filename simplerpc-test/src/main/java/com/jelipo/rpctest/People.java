package com.jelipo.rpctest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jelipo
 * @date 2019/12/20 0:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class People {

    private String name;

    private int age;

    private byte[] array;
}
