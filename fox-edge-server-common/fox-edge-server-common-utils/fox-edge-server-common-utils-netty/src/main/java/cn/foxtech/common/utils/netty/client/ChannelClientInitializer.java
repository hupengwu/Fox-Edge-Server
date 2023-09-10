package cn.foxtech.common.utils.netty.client;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChannelClientInitializer extends io.netty.channel.ChannelInitializer<NioSocketChannel> {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast(new MyMessageEncoder());
//        pipeline.addLast(new MyMessageDecoder());
        pipeline.addLast(new ChannelClientHandler());
    }
}
