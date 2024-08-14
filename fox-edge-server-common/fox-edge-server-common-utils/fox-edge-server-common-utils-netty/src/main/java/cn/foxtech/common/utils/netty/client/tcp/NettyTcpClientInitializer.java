/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.netty.client.tcp;

import cn.foxtech.common.utils.netty.handler.*;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Setter;

public class NettyTcpClientInitializer extends ChannelInitializer<NioSocketChannel> {
    @Setter
    private SplitMessageHandler splitMessageHandler;

    @Setter
    private SocketChannelHandler channelHandler = new SocketChannelHandler();

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new BytesToByteEncoder());

        // 第1道拦截器：沾包拆包工具，根据报文的头标识+帧长度结构，进行拆包/粘包处理
        if (this.splitMessageHandler != null) {
            BeforeBytesDecoder beforeMessageDecoder = new BeforeBytesDecoder();
            beforeMessageDecoder.setHandler(this.splitMessageHandler);
            pipeline.addLast(beforeMessageDecoder);
        }

        // 第2道拦截器：对报文进行提取处理，生成一个byte[]作为完整的一帧数据
        pipeline.addLast(new BytesToByteDecoder());

        ChannelInboundHandler channelInboundHandler = new ChannelInboundHandler();
        channelInboundHandler.setChannelHandler(this.channelHandler);
        pipeline.addLast(channelInboundHandler);
    }
}
