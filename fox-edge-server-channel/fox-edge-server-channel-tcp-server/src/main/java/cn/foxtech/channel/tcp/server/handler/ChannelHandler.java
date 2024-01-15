package cn.foxtech.channel.tcp.server.handler;

import cn.foxtech.channel.socket.core.service.ChannelManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelHandler extends SocketChannelHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHandler.class);

    @Setter
    private RedisConsoleService console;

    @Setter
    private ChannelManager channelManager;

    @Setter
    private ServiceKeyHandler serviceKeyHandler;

    @Setter
    private SessionHandler sessionHandler;

    @Setter
    private ManageHandler manageHandler;


    @Setter
    private boolean logger = false;

    @Setter
    private String returnText;


    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channelManager.insert(ctx);

        // 记录接收到的报文
        String message = "建立连接:" + ctx.channel().remoteAddress();
        LOGGER.info(message);
        console.info(message);
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
            String message = "channelRead: " + ctx.channel().remoteAddress() + ": " + HexUtils.byteArrayToHexString(data);
            LOGGER.info(message);
            console.info(message);
        }


        // 检查：channel是否已经标识上了信息
        String serviceKey = this.channelManager.getServiceKey(ctx.channel().remoteAddress());
        if (serviceKey == null) {
            // 从报文总获得业务特征信息
            serviceKey = this.serviceKeyHandler.getServiceKey(data);

            // 检查：key是否为空
            if (MethodUtils.hasEmpty(serviceKey)) {
                return;
            }

            // 标记:serviceKey信息
            this.channelManager.setServiceKey(ctx, serviceKey);

            // 记录接收到的报文
            String message = "身份识别:" + ctx.channel().remoteAddress() + "; serviceKey=" + serviceKey;
            LOGGER.info(message);
            console.info(message);

            // 发出创建通道的消息
            this.manageHandler.createChannel(serviceKey);
            // 发出创建设备的消息
            this.manageHandler.createDevice(serviceKey);
        }


        // 消息处理
        this.sessionHandler.onMessage(ctx, serviceKey, data);
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx 上下文
     */
    public void channelInactive(final ChannelHandlerContext ctx) {
        this.channelManager.remove(ctx.channel().remoteAddress());

        String message = "连接断开:" + ctx.channel().remoteAddress();
        LOGGER.info(message);
        console.info(message);
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx   上下文
     * @param cause 源头
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message = "连接异常:" + ctx.channel().remoteAddress() + "， cause: " + cause.getMessage();
        LOGGER.error(message);
        console.error(message);
    }
}
