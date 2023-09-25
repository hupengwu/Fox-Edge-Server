package cn.foxtech.common.utils.netty.server.tcp;


import cn.foxtech.common.utils.netty.handler.SocketChannelHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * netty的server
 * 参考文章：https://blog.csdn.net/zwj1030711290/article/details/131590141
 */
public class NettyTcpServer {
    @Getter
    @Setter
    private ChannelInitializer channelInitializer;

    @Getter
    private ChannelFuture channelFuture;

    /**
     * 创建一个TCP SERVER实例
     *
     * @param port                服务端口
     * @param splitMessageHandler 用于帮TCP报文粘包，进行拆包的自定义派生类，它通过报头和报文长度来判定如何拆包
     * @param channelHandler      接收数据的channelHandler派生类
     */
    public static void createServer(int port, SplitMessageHandler splitMessageHandler, SocketChannelHandler channelHandler) {
        NettyTcpChannelInitializer channelInitializer = new NettyTcpChannelInitializer();
        channelInitializer.setChannelHandler(channelHandler);
        channelInitializer.setSplitMessageHandler(splitMessageHandler);

        createServer(port, channelInitializer);
    }

    /**
     * 创建一个TCP SERVER实例
     *
     * @param port               服务端口
     * @param channelInitializer 自定义的通道初始化工具
     */
    public static void createServer(int port, ChannelInitializer channelInitializer) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    NettyTcpServer server = new NettyTcpServer();
                    server.setChannelInitializer(channelInitializer);
                    server.bind(port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    public void bind(int port) {

        /**
         * 配置服务端的NIO线程组
         * NioEventLoopGroup 是用来处理I/O操作的Reactor线程组
         * bossGroup：用来接收进来的连接，workerGroup：用来处理已经被接收的连接,进行socketChannel的网络读写，
         * bossGroup接收到连接后就会把连接信息注册到workerGroup
         * workerGroup的EventLoopGroup默认的线程数是CPU核数的二倍
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            /**
             * ServerBootstrap 是一个启动NIO服务的辅助启动类
             */
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            /**
             * 设置group，将bossGroup， workerGroup线程组传递到ServerBootstrap
             */
            serverBootstrap = serverBootstrap.group(bossGroup, workerGroup);
            /**
             * ServerSocketChannel是以NIO的selector为基础进行实现的，用来接收新的连接，这里告诉Channel通过NioServerSocketChannel获取新的连接
             */
            serverBootstrap = serverBootstrap.channel(NioServerSocketChannel.class);
            /**
             * option是设置 bossGroup，childOption是设置workerGroup
             * netty 默认数据包传输大小为1024字节, 设置它可以自动调整下一次缓冲区建立时分配的空间大小，避免内存的浪费    最小  初始化  最大 (根据生产环境实际情况来定)
             * 使用对象池，重用缓冲区
             */
            serverBootstrap = serverBootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576));
            serverBootstrap = serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576));
            /**
             * 设置 I/O处理类,主要用于网络I/O事件，记录日志，编码、解码消息
             */
            serverBootstrap = serverBootstrap.childHandler(this.channelInitializer);

            System.out.println("netty server " + port + " start success!");
            /**
             * 绑定端口，同步等待成功
             */
            this.channelFuture = serverBootstrap.bind(port).sync();
            /**
             * 等待服务器监听端口关闭
             */
            this.channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {

        } finally {
            /**
             * 退出，释放线程池资源
             */
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}