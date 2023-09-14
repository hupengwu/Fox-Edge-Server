package cn.foxtech.common.utils.netty.server.udp;

import cn.foxtech.common.utils.netty.server.handler.SocketChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyUdpServer {
    private final NettyUdpServerHandler serverHandler = new NettyUdpServerHandler();

    /**
     * 创建一个UDP SERVER实例
     *
     * @param port           服务端口
     * @param channelHandler 接收数据的channelHandler派生类
     */
    public static void createServer(int port, SocketChannelHandler channelHandler) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    NettyUdpServer server = new NettyUdpServer();
                    server.serverHandler.setChannelHandler(channelHandler);
                    server.bind(port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    public void bind(int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)).handler(serverHandler);

            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
