package cn.foxtech.link.tcp2tcp.service;

import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.netty.server.tcp.NettyTcpOriginalChannelInitializer;
import cn.foxtech.common.utils.netty.server.tcp.NettyTcpServer;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.link.common.api.LinkServerAPI;
import cn.foxtech.link.tcp2tcp.entity.Tcp2TcpLinkEntity;
import cn.foxtech.link.tcp2tcp.handler.NorthChannelHandler;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class LinkService extends LinkServerAPI {
    @Getter
    private final Map<String, Tcp2TcpLinkEntity> linkName2linkEntity = new ConcurrentHashMap<>();


    /**
     * 打开通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     */
    @Override
    public void openLink(String linkName, Map<String, Object> linkParam) {
        Integer serverPort = (Integer) linkParam.get("serverPort");
        Map<String, Object> remote = (Map<String, Object>) linkParam.get("remote");
        if (MethodUtils.hasEmpty(serverPort, remote)) {
            throw new ServiceException("参数不能为空: serverPort, remote");
        }

        // 南向参数
        String remoteHost = (String) remote.get("host");
        Integer remotePort = (Integer) remote.get("port");
        if (MethodUtils.hasEmpty(remotePort, remoteHost)) {
            throw new ServiceException("参数不能为空: remote->host, remote->port");
        }

        // 构造实体
        Tcp2TcpLinkEntity entity = new Tcp2TcpLinkEntity();
        entity.setServerPort(serverPort);
        entity.setRemoteHost(remoteHost);
        entity.setRemotePort(remotePort);

        // 保存配置
        this.linkName2linkEntity.put(linkName, entity);

        // 建立【北向】-【连接】-【南向】三个handler

        // 绑定三者的关系
        entity.getNorthChannelHandler().setJoinerChannelHandler(entity.getJoinerChannelHandler());
        entity.getSouthChannelHandler().setJoinerChannelHandler(entity.getJoinerChannelHandler());

        // 创建北向服务
        this.createNorthServer(entity, entity.getNorthChannelHandler());
    }

    private void createNorthServer(Tcp2TcpLinkEntity entity, NorthChannelHandler northChannelHandler) {
        NettyTcpOriginalChannelInitializer channelInitializer = new NettyTcpOriginalChannelInitializer();
        channelInitializer.setChannelHandler(northChannelHandler);

        NettyTcpServer tcpServer = new NettyTcpServer();
        tcpServer.setChannelInitializer(channelInitializer);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpServer.bind(entity.getServerPort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        executorService.shutdown();

        // 记录信息，方便后面关闭服务端口和线程池
        entity.setNorthTcpServer(tcpServer);
        entity.setNorthExecutor(executorService);
    }


    /**
     * 关闭通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     */
    @Override
    public void closeLink(String linkName, Map<String, Object> linkParam) {
        Tcp2TcpLinkEntity linkEntity = this.linkName2linkEntity.get(linkName);

        this.linkName2linkEntity.remove(linkName);

        if (linkEntity == null) {
            return;
        }

        /**
         * 步骤1：关闭南向的连接
         */
        try {
            linkEntity.getSouthChannelFuture().channel().disconnect();
            linkEntity.getSouthChannelFuture().channel().closeFuture();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 步骤2：关闭北向服务端口
         */
        try {
            // 主动关闭服务端口
            linkEntity.getNorthTcpServer().getChannelFuture().channel().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 步骤3：关闭北向监听的线程池
         */
        try {
            // 关闭线程池
            if (!linkEntity.getNorthExecutor().awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                linkEntity.getNorthExecutor().shutdownNow();
            }
        } catch (Exception e) {
            linkEntity.getNorthExecutor().shutdownNow();
            e.printStackTrace();
        }
    }
}
