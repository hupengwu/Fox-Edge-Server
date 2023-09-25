package cn.foxtech.link.tcp2tcp.service;

import cn.foxtech.common.utils.netty.client.tcp.NettyTcpClientFactory;
import cn.foxtech.link.tcp2tcp.entity.Tcp2TcpLinkEntity;
import cn.foxtech.link.tcp2tcp.handler.JoinerChannelHandler;
import cn.foxtech.link.tcp2tcp.handler.SouthChannelHandler;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ServerInitializer {
    @Autowired
    private LinkService linkService;

    public void initialize() {
        ServerInitializer initializer = this;

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        initializer.execute();

                        Thread.sleep(6 * 1000);
                    } catch (Exception e) {
                    }
                }

            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    private void execute() {
        for (String key : this.linkService.getLinkName2linkEntity().keySet()) {
            Tcp2TcpLinkEntity linkEntity = this.linkService.getLinkName2linkEntity().get(key);


            // 获得连接器
            JoinerChannelHandler joinerChannelHandler = linkEntity.getJoinerChannelHandler();
            if (joinerChannelHandler == null) {
                continue;
            }

            // 检查：南向通道是否建立，如果没有建立，那么重新发起连接
            if (joinerChannelHandler.getSouthChannel() == null) {
                ChannelFuture channelFuture = this.connectSouth(linkEntity.getRemoteHost(), linkEntity.getRemotePort(), linkEntity.getSouthChannelHandler());
                linkEntity.setSouthChannelFuture(channelFuture);
             }
        }

    }

    private ChannelFuture connectSouth(String remoteHost, int remotePort, SouthChannelHandler southChannelHandler) {
        NettyTcpClientFactory factory = NettyTcpClientFactory.getInstance();
        factory.getChannelInitializer().setChannelHandler(southChannelHandler);

        return factory.createClient(remoteHost, remotePort);
    }
}
