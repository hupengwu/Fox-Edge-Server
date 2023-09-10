package cn.foxtech.common.utils.netty.server.nettty;

import cn.foxtech.common.utils.netty.server.handler.TcpSocketChannelHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 启动异步线程
 */
public class BootNettyScheduler {
    public void schedule(int port, SplitMessageHandler splitMessageHandler, TcpSocketChannelHandler channelHandler) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    BootNettyServer server = new BootNettyServer();
                    server.getChannelInitializer().setSplitMessageHandler(splitMessageHandler);
                    server.getChannelInitializer().setChannelHandler(channelHandler);
                    server.bind(8888);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }
}
