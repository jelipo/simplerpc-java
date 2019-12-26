package com.jelipo.simplerpc.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chn
 * @date 2019/12/26 16:11
 */
public class NettyIdUtils {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 生成Netty通讯的ID，方便识别往复的内容。
     *
     * @return id。
     */
    public static int generateNettyNetId() {
        if (atomicInteger.get() >= Integer.MAX_VALUE) {
            atomicInteger.set(1);
            return 1;
        }
        return atomicInteger.addAndGet(1);
    }

}
