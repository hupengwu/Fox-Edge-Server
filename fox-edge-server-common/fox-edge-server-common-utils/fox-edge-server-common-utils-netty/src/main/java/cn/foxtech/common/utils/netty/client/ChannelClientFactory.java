package cn.foxtech.common.utils.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Netty客户端工厂：创建异步连接
 */
public class ChannelClientFactory {

    private final static ChannelClientFactory instance = new ChannelClientFactory();

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup group = new NioEventLoopGroup();

    private ChannelClientFactory() {
        this.bootstrap.group(group)// 绑定group
                .channel(NioSocketChannel.class)// 绑定NioSocketChannel
                .option(ChannelOption.SO_KEEPALIVE, true) // 保持心跳
                .option(ChannelOption.TCP_NODELAY, true) // 立即发送
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)// 连接超时
                .handler(new ChannelClientInitializer())
        ;
    }

    public static ChannelClientFactory getInstance() {
        return instance;
    }

    public void createClient(SocketAddress remoteAddress) {
        this.bootstrap.remoteAddress(remoteAddress);
        this.bootstrap.connect().addListener(future -> {
            if (future.cause() != null) {
                //      LOGGER.warn("连接失败: " + future.cause());
            }
        });
    }

    public void createClient(String host, int port) {
        SocketAddress remoteAddress = new InetSocketAddress(host, port);
        this.createClient(remoteAddress);
    }
}
