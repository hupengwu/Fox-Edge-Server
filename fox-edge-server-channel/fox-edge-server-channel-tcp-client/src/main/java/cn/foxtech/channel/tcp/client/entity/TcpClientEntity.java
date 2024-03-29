package cn.foxtech.channel.tcp.client.entity;

import cn.foxtech.channel.tcp.client.handler.ChannelHandler;
import cn.foxtech.common.utils.netty.client.tcp.NettyTcpClientFactory;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketAddress;

@Getter
@Setter
public class TcpClientEntity {
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
     * 通道名称
     */
    private String channelName;
    /**
     * 双工模式：全双工/半双工
     */
    private boolean fullDuplex = false;
    /**
     * 通道连接的Handler
     */
    private ChannelHandler channelHandler;
    /**
     * 独立的tcp客户端工厂
     */
    private NettyTcpClientFactory factory;
    /**
     * 南向TCP连接
     */
    private ChannelFuture channelFuture;
}
