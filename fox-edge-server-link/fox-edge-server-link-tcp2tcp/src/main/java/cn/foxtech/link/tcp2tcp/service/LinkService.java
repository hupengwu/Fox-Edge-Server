package cn.foxtech.link.tcp2tcp.service;

import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.netty.server.tcp.NettyTcpOriginalChannelInitializer;
import cn.foxtech.common.utils.netty.server.tcp.NettyTcpServer;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.link.common.api.LinkServerAPI;
import cn.foxtech.link.common.properties.LinkProperties;
import cn.foxtech.link.tcp2tcp.entity.Tcp2TcpLinkEntity;
import cn.foxtech.link.tcp2tcp.handler.ChannelHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LinkService extends LinkServerAPI {
    private final Map<String, Tcp2TcpLinkEntity> linkName2ServiceKey = new ConcurrentHashMap<>();

    @Autowired
    private LinkManager linkManager;

    @Autowired
    private LinkProperties linkProperties;

    @Autowired
    private PublishService publishService;

    @Autowired
    private ReportService reportService;


    /**
     * 打开通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     */
    @Override
    public void openLink(String linkName, Map<String, Object> linkParam) {
        Map<String, Object> server = (Map<String, Object>) linkParam.get("server");
        Map<String, Object> remote = (Map<String, Object>) linkParam.get("remote");
        if (MethodUtils.hasEmpty(server, remote)) {
            throw new ServiceException("参数不能为空: server, remote");
        }

        // 北向参数
        String serverHost = (String) server.get("host");
        Integer serverPort = (Integer) server.get("port");
        if (MethodUtils.hasEmpty(serverHost, serverPort)) {
            throw new ServiceException("参数不能为空: server->host, server->port");
        }

        // 南向参数
        String remoteHost = (String) remote.get("host");
        Integer remotePort = (Integer) remote.get("port");
        if (MethodUtils.hasEmpty(serverHost, serverPort)) {
            throw new ServiceException("参数不能为空: remote->host, remote->port");
        }

        // 构造实体
        Tcp2TcpLinkEntity entity = new Tcp2TcpLinkEntity();
        entity.setServerHost(serverHost);
        entity.setServerPort(serverPort);
        entity.setRemoteHost(remoteHost);
        entity.setRemotePort(remotePort);

        // 保存配置
        this.linkName2ServiceKey.put(linkName, entity);

        // 创建服务端口
        this.createServer(entity.getServerPort());
    }

    private void createServer(int serverPort){
        // 绑定关系
        ChannelHandler channelHandler = new ChannelHandler();
        channelHandler.setLinkManager(this.linkManager);
        channelHandler.setLogger(this.linkProperties.getLogger());

        NettyTcpOriginalChannelInitializer channelInitializer = new NettyTcpOriginalChannelInitializer();
        channelInitializer.setChannelHandler(channelHandler);

        NettyTcpServer.createServer(serverPort, channelInitializer);
    }

    /**
     * 关闭通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     */
    @Override
    public void closeLink(String linkName, Map<String, Object> linkParam) {
        this.linkName2ServiceKey.remove(linkName);
    }
}
