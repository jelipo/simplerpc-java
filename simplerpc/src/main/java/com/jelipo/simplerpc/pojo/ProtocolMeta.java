package com.jelipo.simplerpc.pojo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ProtocolMeta {

    private boolean hreatBeat;

    private String clientId;

    private int rpcId;

    private Map<String, String> customParams = new HashMap<>();

}
