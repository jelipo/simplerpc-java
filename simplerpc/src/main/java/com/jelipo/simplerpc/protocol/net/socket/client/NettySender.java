package com.jelipo.simplerpc.protocol.net.socket.client;

import com.google.common.cache.Cache;
import com.google.common.primitives.Shorts;
import com.jelipo.simplerpc.core.client.RpcSender;
import com.jelipo.simplerpc.exception.RemoteCallException;
import com.jelipo.simplerpc.pojo.RpcRequest;
import com.jelipo.simplerpc.protocol.net.CommonMetaUtils;
import com.jelipo.simplerpc.protocol.serialization.DataSerialization;
import com.jelipo.simplerpc.util.NettyIdUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 用Netty的主要发送类。
 * 此类保证线程安全。
 *
 * @author Jelipo
 * @date 2019/6/11 2:51
 */
public class NettySender implements RpcSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 每个NettySender实体都有一个随机生成的id，作为CLientId使用。
     */
    private final String clientId = "" + new Random(Integer.MAX_VALUE).nextInt();

    /**
     * 用于发送Netty消息的Channel。
     */
    private List<Channel> channelList;

    private Cache<Integer, CompletableFuture<Object>> cache;

    private DataSerialization dataSerialization;

    public NettySender(NettyClientContext clientContext, List<Channel> channelList) {
        this.channelList = channelList;
        this.cache = clientContext.getCache();
        this.dataSerialization = clientContext.getDataSerialization();
    }

    @Override
    public Object syncSend(RpcRequest rpcRequest) throws Exception {
        CompletableFuture<Object> future = asyncSend(rpcRequest);
        Object result = future.get(10, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public CompletableFuture<Object> asyncSend(RpcRequest rpcRequest) throws Exception {
        int rpcId = NettyIdUtils.generateNettyNetId();

        byte[] metaBytes = CommonMetaUtils.toBytes(false, rpcId, clientId, null);
        byte[] bytes = dataSerialization.serialize(rpcRequest);
        byte[] bytes1 = Shorts.toByteArray((short) metaBytes.length);
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes1, metaBytes, bytes);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        Channel channel = getChannel();
        ChannelFuture channelFuture;

        cache.put(rpcId, completableFuture);

        if (channel.isWritable()) {
            channelFuture = channel.writeAndFlush(byteBuf);
        } else {
            channelFuture = channel.writeAndFlush(byteBuf).sync();
        }
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                //System.out.println("put" + rpcId);
            } else {
                cache.invalidate(rpcId);
                completableFuture.completeExceptionally(new RemoteCallException("Rpc failed to write messages."));
            }
        });
        //channelFuture.sync();
        //channel.();
        return completableFuture;
    }


    @Override
    public void close() {
        for (Channel channel : channelList) {
            channel.close();
        }
    }

    private static final Random random = new Random();

    private Channel getChannel() {
        int i = random.nextInt() & 0xffff;
        return channelList.get(i % channelList.size());
    }

}
