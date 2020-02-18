package com.jelipo.simplerpc.protocol.net.socket.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jelipo.simplerpc.core.client.RpcClientInterface;
import com.jelipo.simplerpc.enums.NetProtocolType;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Jelipo
 * @date 2019/6/28 18:28
 */
public class NettyClient implements RpcClientInterface {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private NettyClientConfig config;

    /**
     * 序列化/反序列化处理器。
     */
    private DataSerialization dataSerialization;

    /**
     * 缓存的时间，单位 秒。
     */
    private int cacheTime = 300;

    private NettyClientContext clientContext;

    private NettySender nettySender;

    /**
     * 用于存放 释放锁对象 的缓存。
     */
    private Cache<Integer, CompletableFuture<Object>> cache = CacheBuilder.newBuilder()
            .maximumSize(Integer.MAX_VALUE)
            .expireAfterWrite(cacheTime, TimeUnit.SECONDS)
            .build();

    private int connectNum = 6;

    /**
     * 此构造方法会直接创建一个与Netty Server 的连接。
     *
     * @param nettyClientConfig netty客户端的配置信息。
     * @param dataSerialization 序列化处理器。
     * @throws InterruptedException 启动netty时造成的异常。
     */
    public NettyClient(NettyClientConfig nettyClientConfig, DataSerialization dataSerialization) throws InterruptedException {
        this.config = nettyClientConfig;
        this.dataSerialization = dataSerialization;
        this.buildDefaultSender();
    }

    private void buildDefaultSender() throws InterruptedException {
        this.clientContext = new NettyClientContext(dataSerialization, config, cache);

        List<Channel> channelList = new ArrayList<>(connectNum);

        for (int i = 0; i < connectNum; i++) {
            Channel channel = start(0);
            if (channel.isOpen()) {
                channelList.add(channel);
            } else {
                logger.warn("有连接不可用");
            }
        }
        //NetProtocolType.DEFAULT.get().newInstance()
        nettySender = new NettySender(clientContext, channelList);


    }

    /**
     * 位于内部的主要启动方法。
     *
     * @param retryNum 重试次数，根据已经重试的次数判断是否是需要继续重试。
     */
    private Channel start(int retryNum) throws InterruptedException {

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        int lengthFieldLength = config.getLengthFieldLength();
                        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(config.getNettyMaxFrameLength(), 0, lengthFieldLength, 0, lengthFieldLength);
                        LengthFieldPrepender prepender = new LengthFieldPrepender(lengthFieldLength);
                        ch.pipeline().addLast(decoder, prepender);
                        ch.pipeline().addLast(new IdleStateHandler(0, 4, 0));
                        ch.pipeline().addLast(new NettySenderHandler(clientContext));
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(config.getHost(), config.getPort());
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                logger.warn("Connection failed. Try reconnecting in 2 seconds.");
                Thread.sleep(2000);
                start(retryNum + 1);
            }
        });
        // 同步等待启动客户端完成。
        return channelFuture.channel();
    }


    @Override
    public NettySender getNettySender() {
        return nettySender;
    }

}
