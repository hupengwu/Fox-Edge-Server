package cn.foxtech.common.utils.netty.demo1;


import cn.foxtech.common.utils.netty.client.ChannelClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);


    public static void main(String[] args) throws Exception {

        ChannelClientManager.registerRemoteAddress("localhost", 7000);
        ChannelClientManager.registerRemoteAddress("localhost", 7001);
        ChannelClientManager.registerRemoteAddress("localhost", 7002);
//        ChannelManager.registerRemoteAddress("127.0.0.1", 7002);
//        ChannelManager.registerRemoteAddress("127.0.0.1", 7003);
//        ChannelManager.registerRemoteAddress("127.0.0.1", 7004);

        ChannelClientManager.schedule();

      //  NioEventLoopGroup group = new NioEventLoopGroup();

        try {
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7000);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7001);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7002);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7003);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7004);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7005);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7006);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7007);
//            NettyClientFactory.getInstance().createClient("127.0.0.1", 7008);


//
//            Bootstrap bootstrap = new Bootstrap();
//
//            bootstrap.group(group)
//                    .channel(NioSocketChannel.class)
//                    .handler(new NettyChannelInitializer());
//
//            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 7000).sync();
//            channelFuture.channel().closeFuture().sync();
        } finally {
            //        NettyClientFactory.getInstance().shutdown();
            //      group.shutdownGracefully();
        }
    }

    public void createThread() {
        CachedThreadPool.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {

                } catch (Exception e) {
                    LOGGER.warn("启动任务异常：", e);
                }
            }
        });
    }
}
