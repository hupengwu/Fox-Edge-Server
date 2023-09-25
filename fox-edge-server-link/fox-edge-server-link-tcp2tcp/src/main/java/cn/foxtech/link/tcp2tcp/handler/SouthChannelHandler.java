package cn.foxtech.link.tcp2tcp.handler;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SouthChannelHandler extends SocketChannelHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NorthChannelHandler.class);

    @Setter
    private JoinerChannelHandler joinerChannelHandler;

    @Autowired
    private RedisConsoleService consoleService;


    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String info = "南向建立连接:" + ctx.channel().remoteAddress();

        LOGGER.info(info);
        this.consoleService.info(info);

        this.joinerChannelHandler.insertSouthChannel(ctx);
    }

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx 上下文
     * @param msg 信息
     */
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.joinerChannelHandler.send2North(msg);
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx 上下文
     */
    public void channelInactive(final ChannelHandlerContext ctx) {
        String info = "南向连接断开:" + ctx.channel().remoteAddress();

        LOGGER.info(info);
        this.consoleService.info(info);

        this.joinerChannelHandler.removeSouthChannel(ctx);
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx   上下文
     * @param cause 源头
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String info = "南向连接异常:" + ctx.channel().remoteAddress();

        LOGGER.info(info);
        this.consoleService.info(info);
    }
}
