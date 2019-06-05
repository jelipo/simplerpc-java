package com.springmarker.simplerpc.protocol.serialization.json;

import com.springmarker.simplerpc.pojo.RpcRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: Springmarker
 * @date: 2019/5/29 3:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RpcRequestJsonExchange extends RpcRequest {

    private List<String> exchangeStrParams;

}
