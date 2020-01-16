package com.jelipo.simplerpc.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author chn
 * @date 2020/1/16 22:24
 */
public class RpcInterfaceManager {

    private static final Logger logger = LoggerFactory.getLogger(RpcInterfaceManager.class);

    private Set<Class> rpcInterfaceSet = new HashSet<>();


    public Set<Class> getRpcInterfaceSet() {
        return rpcInterfaceSet;
    }

    public void add(Class clazz) {
        rpcInterfaceSet.add(clazz);
        logger.debug("Add a rpc impl class:" + clazz.toString());
    }

}