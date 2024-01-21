package cn.foxtech.channel.udp.server.handler;

import cn.foxtech.channel.udp.server.service.ChannelManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
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

        // 从缓存中取出数据
        DatagramPacket packet = (DatagramPacket) msg;
        int size = packet.content().readableBytes();
        byte[] data = new byte[size];
        packet.content().readBytes(data);

        // 记录接收到的报文
        if (this.logger) {
            String message = "channelRead: " + packet.sender() + ": " + HexUtils.byteArrayToHexString(data);
            LOGGER.info(message);
            console.info(message);
        }

        // 检查：channel是否已经标识上了信息
        String serviceKey = this.channelManager.getServiceKey(packet.sender());
        if (serviceKey == null) {

            // 从报文总获得业务特征信息
            serviceKey = this.serviceKeyHandler.getServiceKey(data);

            // 检查：key是否为空
            if (MethodUtils.hasEmpty(serviceKey)) {
                return;
            }

            // 标记:serviceKey信息
            // 注意：UDP场景下，因为无连接，它的SocketAddress在报文信息上，而不在ctx连接上
            this.channelManager.setServiceKey(packet.sender(), ctx, serviceKey);

            // 记录接收到的报文
            String message = "设备接入:" + packet.sender() + "; serviceKey=" + serviceKey;
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
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx   上下文
     * @param cause 源头
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message = "连接异常:" + ctx.channel().remoteAddress();
        LOGGER.error(message);
        console.error(message);
    }
}
