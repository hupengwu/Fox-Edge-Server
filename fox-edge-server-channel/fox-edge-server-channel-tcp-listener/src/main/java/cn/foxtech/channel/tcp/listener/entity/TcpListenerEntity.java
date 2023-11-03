package cn.foxtech.channel.tcp.listener.entity;

import cn.foxtech.channel.tcp.listener.handler.ChannelHandler;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketAddress;

@Getter
@Setter
public class TcpListenerEntity {
    /**
     * 设备的IP
     */
    private String remoteHost;
    /**
     * 设备的端口
     */
    private Integer remotePort;
    /**
     * socket地址
     */
    private SocketAddress socketAddress;
    /**
     * java的拆包类
     */
    private SplitMessageHandler splitMessageHandler;
    /**
     * java的身份识别类
     */
    private ServiceKeyHandler serviceKeyHandler;
    /**
     * 设备在报文中夹带的身份特征
     */
    private String serviceKey;
    /**
     * 建立连接的socket通道
     */
    private ChannelHandlerContext channelHandlerContext;
    /**
     * 通道连接的Handler
     */
    private ChannelHandler channelHandler;
    /**
     * 南向TCP连接
     */
    private ChannelFuture channelFuture;
}
