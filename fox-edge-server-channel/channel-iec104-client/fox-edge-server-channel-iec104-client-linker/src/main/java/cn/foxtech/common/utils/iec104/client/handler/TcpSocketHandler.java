package cn.foxtech.common.utils.iec104.client.handler;

import cn.foxtech.common.utils.iec104.client.Iec104ClientLinkerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpSocketHandler extends SimpleChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpSocketHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("建立连接:" + ctx.channel().remoteAddress());

        // 标识为连接状态
        Iec104ClientLinkerManager.updateEntity4IsConnected(ctx.channel());

        ctx.fireChannelActive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOGGER.info("连接断开:" + ctx.channel().remoteAddress());

        // 标识为初始状态
        Iec104ClientLinkerManager.updateEntity4WaitConnected(ctx.channel());

        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("连接异常:" + ctx.channel().remoteAddress());

        ctx.fireExceptionCaught(cause);
    }
}
