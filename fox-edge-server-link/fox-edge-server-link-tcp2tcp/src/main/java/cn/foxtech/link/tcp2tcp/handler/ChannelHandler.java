package cn.foxtech.link.tcp2tcp.handler;

import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.link.tcp2tcp.service.LinkManager;
import cn.foxtech.link.tcp2tcp.service.ReportService;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelHandler extends SocketChannelHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHandler.class);

    @Setter
    private LinkManager linkManager;

    @Setter
    private ReportService reportService;

    @Setter
    private ServiceKeyHandler serviceKeyHandler;

    @Setter
    private boolean logger = false;


    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.linkManager.insert(ctx);

        LOGGER.info("建立连接:" + ctx.channel().remoteAddress());

    }

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx 上下文
     * @param msg 信息
     */
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;

    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx 上下文
     */
    public void channelInactive(final ChannelHandlerContext ctx) {
        this.linkManager.remove(ctx);

        LOGGER.info("连接断开:" + ctx.channel().remoteAddress());
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx   上下文
     * @param cause 源头
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("连接异常:" + ctx.channel().remoteAddress());
    }
}
