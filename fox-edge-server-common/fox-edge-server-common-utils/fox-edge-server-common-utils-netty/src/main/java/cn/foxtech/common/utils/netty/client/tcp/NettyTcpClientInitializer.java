package cn.foxtech.common.utils.netty.client.tcp;

import cn.foxtech.common.utils.netty.handler.BytesToByteDecoder;
import cn.foxtech.common.utils.netty.handler.BytesToByteEncoder;
import cn.foxtech.common.utils.netty.handler.ChannelInboundHandler;
import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;

public class NettyTcpClientInitializer extends ChannelInitializer<NioSocketChannel> {
    @Setter
    private SocketChannelHandler channelHandler = new SocketChannelHandler();

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new BytesToByteEncoder());
        pipeline.addLast(new BytesToByteDecoder());

        ChannelInboundHandler channelInboundHandler = new ChannelInboundHandler();
        channelInboundHandler.setChannelHandler(this.channelHandler);
        pipeline.addLast(channelInboundHandler);
    }
}
