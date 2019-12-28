package com.jelipo.simplerpc.protocol.common;

import java.util.concurrent.CompletableFuture;

/**
 * @author Jelipo
 * @date 2019/12/26 22:45
 */
public interface ConnectStarter<T> {

    CompletableFuture<BaseConnect<T>> creatConnect();

}
