package com.springmarker.simplerpc.protocol.net.netty;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
public class NettyHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private Cache<ChannelHandlerContext, AtomicInteger> cache;

    private int retryMaxTimes;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    NettyHeartBeatHandler(IdleStateHandler idleStateHandler, int retryMaxTimes) {
        long readerIdleTimeInMillis = idleStateHandler.getReaderIdleTimeInMillis();
        this.retryMaxTimes = retryMaxTimes;
        this.cache = Caffeine.newBuilder()
                .maximumSize(Integer.MAX_VALUE)
                .expireAfterWrite(readerIdleTimeInMillis * (retryMaxTimes + 2), TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 在此handler中，此方法主要用于出现 心跳包未定期发送过来 的处理方法。
     *
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.debug("Heart beat acquisition Timeout.");
        //如果不是IdleStateEvent类直接跳过
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
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
            cache.invalidate(ctx);
            ctx.channel().close();
            logger.debug("Over the maximum number of retries,close the channel:" + ctx.toString());
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AtomicInteger retryTimesAtomic = cache.getIfPresent(ctx);
        if (retryTimesAtomic != null) {
            retryTimesAtomic.set(0);
        }
        logger.debug("Get the latest information ,clearing up 'retryTimes'.");
        super.channelRead(ctx, msg);
    }
}
