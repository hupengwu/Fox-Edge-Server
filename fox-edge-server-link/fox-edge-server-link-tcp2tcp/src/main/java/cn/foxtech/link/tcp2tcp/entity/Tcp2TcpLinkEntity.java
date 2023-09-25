package cn.foxtech.link.tcp2tcp.entity;

import cn.foxtech.common.utils.netty.server.tcp.NettyTcpServer;
import cn.foxtech.link.tcp2tcp.handler.JoinerChannelHandler;
import cn.foxtech.link.tcp2tcp.handler.NorthChannelHandler;
import cn.foxtech.link.tcp2tcp.handler.SouthChannelHandler;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledExecutorService;

@Getter
@Setter
public class Tcp2TcpLinkEntity {
    // 北向参数
    private Integer serverPort;

    // 南向参数
    private String remoteHost;
    private Integer remotePort;

    // 连接器：南北向的连接器信息，方便后面南向客户端的的自动连接
    private JoinerChannelHandler joinerChannelHandler = new JoinerChannelHandler();

    private NorthChannelHandler northChannelHandler = new NorthChannelHandler();

    /**
     * 南向的ChannelHandler
     */
    private SouthChannelHandler southChannelHandler = new SouthChannelHandler();

    /**
     * 南向TCP连接
     */
    private ChannelFuture southChannelFuture;

    /**
     * 北向TcpServer实例
     */
    private NettyTcpServer northTcpServer;
    /**
     * 北向异步线程池
     */
    private ScheduledExecutorService northExecutor;
}
