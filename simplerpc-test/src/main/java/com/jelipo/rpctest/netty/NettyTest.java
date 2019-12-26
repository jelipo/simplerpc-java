package com.jelipo.rpctest.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author Jelipo
 * @date 2019/12/24 22:07
 */
public class NettyTest {

    public static void main(String[] args) throws InterruptedException {
        NettyTest nettyTest = new NettyTest();
        nettyTest.startServer();

        Channel channel = nettyTest.startClient();

        Thread.sleep(5000);

        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {

            ByteBuf byteBuf = Unpooled.copiedBuffer(new byte[140]);

            ChannelFuture channelFuture = channel.writeAndFlush(byteBuf);
            int finalI = i;
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (finalI == 99999) {
                    System.out.println("finish:" + (System.currentTimeMillis() - l));
                }
            });
        }
        System.out.println("finish:" + (System.currentTimeMillis() - l));
        Thread.sleep(1000000);
    }

    private void startServer() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(18088))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        nettyChannelInit(ch);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
    }

    private Channel startClient() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        int lengthFieldLength = 4;
                        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(1024 * 1024, 0, lengthFieldLength, 0, lengthFieldLength);
                        LengthFieldPrepender prepender = new LengthFieldPrepender(lengthFieldLength);
                        ch.pipeline().addLast(decoder, prepender);
                        ch.pipeline().addLast(new IdleStateHandler(0, 4, 0));
//                        ch.pipeline().addLast(new NettySenderHandler(clientContext));
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect("localhost", 18088);
        channelFuture.addListener((ChannelFutureListener) future -> {
            System.out.println(future.isSuccess());
        });
        // 同步等待启动客户端完成。
        return channelFuture.channel();
    }

    private void nettyChannelInit(SocketChannel ch) throws Exception {
        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4);
        LengthFieldPrepender prepender = new LengthFieldPrepender(4);
        ch.pipeline().addLast(decoder, prepender);
        IdleStateHandler idleStateHandler = new IdleStateHandler(5, 0, 0);
        ch.pipeline().addLast(idleStateHandler);

        ch.pipeline().addLast(new Handler());
    }

}
