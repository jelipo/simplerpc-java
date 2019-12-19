package com.jelipo.simplerpc.protocol.net.socket.server;

import com.google.common.collect.Lists;
import com.jelipo.simplerpc.core.server.AbstractRpcServer;
import com.jelipo.simplerpc.core.server.ProxyServerCore;
import com.jelipo.simplerpc.pojo.ServerConfig;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Netty的Server启动类。
 *
 * @author Jelipo
 * @date 2019/6/11 2:52
 */
public class NettyRpcServer extends AbstractRpcServer {

    private int retryTimes = 5;

    private int heartBeatWaitTimeSeconds = 5;

    /**
     * Netty单次发送/接收 最大的字节数。
     */
    private int nettyMaxFrameLength = 1024 * 1024;

    public NettyRpcServer(ServerConfig config, ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        super(config, proxyServerCore, dataSerialization);

    }

    @Override
    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(super.config.getPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        nettyChannelInit(ch);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind().sync();

    }

    private void nettyChannelInit(SocketChannel ch) throws Exception {
        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(nettyMaxFrameLength, 0, 4, 0, 4);
        LengthFieldPrepender prepender = new LengthFieldPrepender(4);
        ch.pipeline().addLast(decoder, prepender);
        IdleStateHandler idleStateHandler = new IdleStateHandler(heartBeatWaitTimeSeconds, 0, 0);
        ch.pipeline().addLast(idleStateHandler);
        NettyRpcWorker nettyRpcWorker = new NettyRpcWorker(proxyServerCore, dataSerialization);
        NettyHeartBeatWorker nettyHeartBeatWorker = new NettyHeartBeatWorker(idleStateHandler, retryTimes);

        //确保先后顺序
        ArrayList<NettyWorker> workerList = Lists.newArrayList(nettyHeartBeatWorker, nettyRpcWorker);

        ch.pipeline().addLast(new NettyServerMainHandler(workerList, nettyRpcWorker, nettyHeartBeatWorker, dataSerialization));
    }


}
