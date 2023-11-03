package cn.foxtech.channel.tcp.listener.handler;

import cn.foxtech.channel.tcp.listener.service.ChannelManager;
import cn.foxtech.channel.tcp.listener.service.ReportService;
import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelHandler extends SocketChannelHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHandler.class);

    @Setter
    private ChannelManager channelManager;

    @Setter
    private ReportService reportService;

    /**
     * java的拆包类
     */
    @Getter
    @Setter
    private SplitMessageHandler splitMessageHandler;

    /**
     * java的身份识别类
     */
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
        LOGGER.info("建立连接:" + ctx.channel().remoteAddress());

        this.channelManager.insert(ctx);

    }

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx 上下文
     * @param msg 信息
     */
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.serviceKeyHandler == null) {
            return;
        }

        byte[] data = (byte[]) msg;

        // 记录接收到的报文
        if (this.logger) {
            LOGGER.info("channelRead: " + ctx.channel().remoteAddress() + ": " + HexUtils.byteArrayToHexString(data));
        }


        // 检查：channel是否已经标识上了信息
        String serviceKey = this.channelManager.getServiceKey(ctx.channel().remoteAddress());
        if (serviceKey == null) {
            // 从报文总获得业务特征信息
            serviceKey = this.serviceKeyHandler.getServiceKey(data);

            // 标记:serviceKey信息
            this.channelManager.setServiceKey(ctx, serviceKey);
        }

        // 保存PDU到接收缓存
        this.reportService.push(serviceKey, (byte[]) msg);
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx 上下文
     */
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOGGER.info("连接断开:" + ctx.channel().remoteAddress());
        this.channelManager.remove(ctx);
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
