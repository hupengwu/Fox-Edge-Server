package cn.foxtech.channel.hikvision.fire.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.hikvision.fire.handler.ChannelHandler;
import cn.foxtech.channel.hikvision.fire.handler.SessionHandler;
import cn.foxtech.channel.socket.core.service.ChannelManager;
import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.netty.server.tcp.NettyTcpServer;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.handler.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.handler.SplitMessageHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 启动TCP服务器的异步线程
 */
@Component
public class ServerInitializer {
    private final Logger logger = Logger.getLogger(this.getClass());
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ChannelProperties channelProperties;


    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private ReportService reportService;


    @Autowired
    private ChannelManager channelManager;


    public void initialize() {
        // 读取配置参数
        this.localConfigService.initialize();
        Map<String, Object> configs = this.localConfigService.getConfig();

        // 记录启动参数，方便后面全局使用
        this.channelProperties.setLogger((Boolean) configs.getOrDefault("logger", false));

        // 启动多个服务器
        this.startTcpServer(configs);
    }

    /**
     * 启动一个TCP Server
     *
     * @param config 配置参数项目
     */
    private void startTcpServer(Map<String, Object> config) {
        try {
            Integer serverPort = (Integer) config.getOrDefault("serverPort", 9311);

            // 底层的分拆和身份识别handler
            SplitMessageHandler splitMessageHandler = new SplitMessageHandler();
            ServiceKeyHandler serviceKeyHandler = new ServiceKeyHandler();

            SessionHandler sessionHandler = new SessionHandler();
            sessionHandler.setReportService(this.reportService);
            sessionHandler.setLogger(this.channelProperties.isLogger());
            sessionHandler.setConsole(this.console);

            // 绑定关系
            ChannelHandler channelHandler = new ChannelHandler();
            channelHandler.setServiceKeyHandler(serviceKeyHandler);
            channelHandler.setChannelManager(this.channelManager);
            channelHandler.setLogger(this.channelProperties.isLogger());
            channelHandler.setConsole(this.console);
            channelHandler.setSessionHandler(sessionHandler);

            // 创建一个Tcp Server实例
            NettyTcpServer.createServer(serverPort, splitMessageHandler, channelHandler);
        } catch (Exception e) {
            String message = "scanJarFile出现异常:" + e.getMessage();
            this.logger.error(message);
            this.console.error(message);
        }
    }
}
