package com.springmarker.simplerpc.protocol.net.netty.server;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.springmarker.simplerpc.pojo.ExchangeRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 专门处理心跳包超时的处理类。
 *
 * @author Springmarker
 * @date 2019/6/22 20:05
 */
public class NettyHeartBeatWorker implements NettyWorker {

    private Cache<ChannelHandlerContext, AtomicInteger> cache;

    private int retryMaxTimes;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    NettyHeartBeatWorker(IdleStateHandler idleStateHandler, int retryMaxTimes) {
        long readerIdleTimeInMillis = idleStateHandler.getReaderIdleTimeInMillis();
        this.retryMaxTimes = retryMaxTimes;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(Integer.MAX_VALUE)
                .expireAfterWrite(readerIdleTimeInMillis * (retryMaxTimes + 2), TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 在此handler中，此方法主要用于出现 心跳包未定期发送过来 的处理方法。
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.debug("Heart beat acquisition Timeout.");
        //如果不是IdleStateEvent类直接跳过
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent event = (IdleStateEvent) evt;

        if (event.state() != IdleState.READER_IDLE) {
            return;
        }
        AtomicInteger retryTimesAtomic = cache.getIfPresent(ctx);
        if (retryTimesAtomic == null) {
            retryTimesAtomic = new AtomicInteger(0);
            cache.put(ctx, retryTimesAtomic);
        }
        //已经自增过的重试次数
        int retryTimes = retryTimesAtomic.addAndGet(1);
        //遇到超过次数了，直接删除缓存和关闭channel连接。
        if (retryTimes >= retryMaxTimes) {
            //如果次数超过了，直接关闭。
            logger.debug("Over the maximum number of retries,close the channel:" + ctx.toString());
            cache.invalidate(ctx);
            ctx.channel().close();
        }

    }

    @Override
    public boolean handle(ChannelHandlerContext ctx, ExchangeRequest exchangeRequest) throws Exception {
        AtomicInteger retryTimesAtomic = cache.getIfPresent(ctx);
        if (retryTimesAtomic != null) {
            retryTimesAtomic.set(0);
        }
        if (exchangeRequest.getStatus() == 0) {
            logger.trace("Get the latest information ,clearing up 'retryTimes'.");
            return true;
        }
        return false;
    }

    private static final byte[] HEART_BEAT_BYTES = "HEART".getBytes();

    private boolean isHeartBeatPackage(byte[] bytes) {
        if (bytes.length != HEART_BEAT_BYTES.length) {
            return false;
        }
        int n = HEART_BEAT_BYTES.length;
        int i = 0;
        while (n-- != 0) {
            if (HEART_BEAT_BYTES[i] != bytes[i]) {
                return false;
            }
            i++;
        }
        return true;
    }
}
