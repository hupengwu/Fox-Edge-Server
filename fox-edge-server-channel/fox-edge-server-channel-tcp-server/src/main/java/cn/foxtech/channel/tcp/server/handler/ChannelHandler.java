package cn.foxtech.channel.tcp.server.handler;

import cn.foxtech.channel.socket.core.service.ChannelManager;
import cn.foxtech.channel.tcp.server.service.ReportService;
import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;

public class ChannelHandler extends SocketChannelHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHandler.class);

    @Setter
    private ChannelManager channelManager;

    @Setter
    private ReportService reportService;

    @Setter
    private ServiceKeyHandler serviceKeyHandler;

    @Setter
    private boolean logger = false;

    @Setter
    private String returnText = "";


    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channelManager.insert(ctx);

        LOGGER.info("建立连接:" + ctx.channel().remoteAddress());

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

            // 检查：key是否为空
            if (MethodUtils.hasEmpty(serviceKey)){
                return;
            }

            // 标记:serviceKey信息
            this.channelManager.setServiceKey(ctx, serviceKey);
        }

        if (MethodUtils.hasEmpty(this.returnText)){
            // 保存PDU到接收缓存
            this.reportService.push(serviceKey, (byte[]) msg);
        }
        else {
            String message = new String((byte[]) msg, returnText);
            this.reportService.push(serviceKey, message);
        }


    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx 上下文
     */
    public void channelInactive(final ChannelHandlerContext ctx) {
        this.channelManager.remove(ctx.channel().remoteAddress());

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
