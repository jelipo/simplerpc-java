package com.springmarker.simplerpc.protocol.net.http.pojo;

import com.springmarker.simplerpc.pojo.ExchangeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Springmarker
 * @date 2018/10/24 23:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class HttpExchangeRequest extends ExchangeRequest {
    private String type;
    private int methodHashCode;
    private List<Object> paramList;
    private int needReturn;

}