package cn.foxtech.common.utils.iec104.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Netty客户端工厂：创建异步连接
 */
public class Iec104ClientLinkerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104ClientLinkerFactory.class);

    private final static Iec104ClientLinkerFactory instance = new Iec104ClientLinkerFactory();

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup group = new NioEventLoopGroup();

    private final Iec104ClientLinkerScheduler scheduler = new Iec104ClientLinkerScheduler();

    private Iec104ClientLinkerFactory() {
        this.bootstrap.group(group)// 绑定group
                .channel(NioSocketChannel.class)// 绑定NioSocketChannel
                .option(ChannelOption.SO_KEEPALIVE, true) // 保持心跳
                .option(ChannelOption.TCP_NODELAY, true) // 立即发送
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)// 连接超时
                .handler(new Iec104ClientLinkerInitializer());
    }

    public static Iec104ClientLinkerFactory getInstance() {
        return instance;
    }

    private Iec104ClientLinkerScheduler getScheduler() {
        return this.scheduler;
    }

    public void createClient(SocketAddress remoteAddress) {
        this.bootstrap.remoteAddress(remoteAddress);
        this.bootstrap.connect().addListener(future -> {
            if (!future.isSuccess()) {
                LOGGER.info("连接失败:" + remoteAddress);
            }
        });
    }

    public void createClient(String host, int port) {
        SocketAddress remoteAddress = new InetSocketAddress(host, port);
        this.createClient(remoteAddress);
    }
}
