package cn.foxtech.common.utils.iec104.client;

import cn.foxtech.common.utils.iec104.client.handler.*;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Iec104ClientLinkerInitializer extends io.netty.channel.ChannelInitializer<NioSocketChannel> {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 第1道拦截器：沾包拆包工具，根据报文的0x68+帧长度结构，进行拆包/粘包处理
        pipeline.addLast(new BeforePackHandler());

        // 第2道拦截器：对报文进行解码处理，生成ADPU实体
        pipeline.addLast(new MessageDecoder());

        // 第3道拦截器：对APDU实体进行处理
        pipeline.addLast(new MessageHandler());

        // byte[]的编码器：将byte[]报文，编码成待发送的TCP数据流
        pipeline.addLast(new BytesToByteEncoder());

        // TCP连接流程的响应处理
        pipeline.addLast(new TcpSocketHandler());
    }
}
