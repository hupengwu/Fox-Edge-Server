/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.netty.server.tcp;


import cn.foxtech.common.utils.netty.handler.*;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.Setter;

/**
 * 通道初始化
 */
public class NettyTcpChannelInitializer<SocketChannel> extends ChannelInitializer<Channel> {
    @Setter
    private SplitMessageHandler splitMessageHandler = new SplitMessageHandler();

    @Setter
    private SocketChannelHandler channelHandler = new SocketChannelHandler();


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 出方向：byte[]的编码器：将byte[]报文，编码成待发送的TCP数据流
        pipeline.addLast(new BytesToByteEncoder());

        // 入方向：多个ChannelHandler组成链条，按顺序进行处理

        // 第1道拦截器：沾包拆包工具，根据报文的头标识+帧长度结构，进行拆包/粘包处理
        BeforeBytesDecoder beforeMessageDecoder = new BeforeBytesDecoder();
        beforeMessageDecoder.setHandler(this.splitMessageHandler);
        pipeline.addLast(beforeMessageDecoder);

        // 第2道拦截器：对报文进行提取处理，生成一个byte[]作为完整的一帧数据
        pipeline.addLast(new BytesToByteDecoder());

        // 第3道拦截器：TCP连接流程的响应处理
        ChannelInboundHandler inboundHandler = new ChannelInboundHandler();
        inboundHandler.setChannelHandler(this.channelHandler);
        pipeline.addLast(inboundHandler);
    }
}