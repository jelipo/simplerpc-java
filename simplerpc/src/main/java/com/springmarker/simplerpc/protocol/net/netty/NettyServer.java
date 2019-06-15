package com.springmarker.simplerpc.protocol.net.netty;

import com.springmarker.simplerpc.core.server.AbstractServer;
import com.springmarker.simplerpc.core.server.ProxyServerCore;
import com.springmarker.simplerpc.pojo.ServerConfig;
import com.springmarker.simplerpc.protocol.serialization.DataSerialization;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Netty的Server启动类。
 *
 * @author: Springmarker
 * @date: 2019/6/11 2:52
 */
public class NettyServer extends AbstractServer {

    private NettyEchoServerHandler echoServerHandler;

    public NettyServer(ServerConfig config, ProxyServerCore proxyServerCore, DataSerialization dataSerialization) {
        super(config, proxyServerCore, dataSerialization);
        echoServerHandler = new NettyEchoServerHandler(proxyServerCore, dataSerialization);
    }

    @Override
    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(super.config.getPort()))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }

}
