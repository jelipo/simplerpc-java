package com.springmarker.simplerpc.protocol.serialization.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专门用于传输。
 *
 * @author: Springmarker
 * @date: 2019/5/29 3:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeParam {

    /**
     * 全程"className"
     */
    private Class c;

    private byte[] value;

}
