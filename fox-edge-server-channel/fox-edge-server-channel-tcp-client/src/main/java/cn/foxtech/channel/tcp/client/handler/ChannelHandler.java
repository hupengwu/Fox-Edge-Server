package cn.foxtech.channel.tcp.client.handler;

import cn.foxtech.channel.tcp.client.service.ChannelManager;
import cn.foxtech.channel.tcp.client.service.ReportService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * socket触发响应的Handler
 */
public class ChannelHandler extends SocketChannelHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHandler.class);

    @Setter
    private ChannelManager channelManager;

    @Setter
    private ReportService reportService;

    /**
     * 双工模式：全双工/半双工
     */
    @Setter
    private boolean fullDuplex = false;

    /**
     * java的拆包类
     */
    @Getter
    @Setter
    private SplitMessageHandler splitMessageHandler;


    @Setter
    private boolean logger = false;

    @Setter
    private RedisConsoleService consoleService;

    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String message = "建立连接:" + ctx.channel().remoteAddress();
        LOGGER.info(message);
        this.consoleService.info(message);

        this.channelManager.insert(ctx);
        this.channelManager.setServiceKey(ctx, ctx.channel().remoteAddress().toString());
    }

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx 上下文
     * @param msg 信息
     */
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;

        // 记录接收到的报文
        if (this.logger) {
            LOGGER.info("channelRead: " + ctx.channel().remoteAddress() + ": " + HexUtils.byteArrayToHexString(data));
        }


        if (this.fullDuplex) {
            // 全双工模式：用host:port作为业务特征
            String serviceKey = ctx.channel().remoteAddress().toString();

            // 保存PDU到接收缓存，由reportService主动上报
            this.reportService.push(serviceKey, (byte[]) msg);
        } else {
            // 半双工模式：用host:port作为业务特征
            String serviceKey = ctx.channel().remoteAddress().toString();

            // 通知数据到达
            SyncFlagObjectMap.inst().notifyConstant(serviceKey, data);
        }

    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx 上下文
     */
    public void channelInactive(final ChannelHandlerContext ctx) {
        String message = "连接断开::" + ctx.channel().remoteAddress();
        LOGGER.info(message);
        this.consoleService.info(message);

        this.channelManager.remove(ctx);
        this.channelManager.remove(ctx.channel().remoteAddress().toString());
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx   上下文
     * @param cause 源头
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message = "连接异常::" + ctx.channel().remoteAddress();
        LOGGER.info(message);
        this.consoleService.info(message);
    }
}
