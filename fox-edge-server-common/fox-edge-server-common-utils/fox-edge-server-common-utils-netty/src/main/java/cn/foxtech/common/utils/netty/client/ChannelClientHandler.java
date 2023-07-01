package cn.foxtech.common.utils.netty.client;

import cn.foxtech.common.utils.netty.demo1.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ChannelClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("建立连接:" + ctx.channel().remoteAddress());
        ChannelClientManager.channelActive(ctx.channel());


        // 发送一组数据
        for (int i = 0; i < 10; i++) {
            String message = "今天天气冷，吃火锅";
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            int len = bytes.length;
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setLen(len);
            messageProtocol.setContent(bytes);
            ctx.writeAndFlush(messageProtocol);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOGGER.info("连接断开:" + ctx.channel().remoteAddress());
        ChannelClientManager.channelInactive(ctx.channel());
    }
}
