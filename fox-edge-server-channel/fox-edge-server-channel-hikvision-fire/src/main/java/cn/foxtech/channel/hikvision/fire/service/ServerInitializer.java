package cn.foxtech.channel.hikvision.fire.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.hikvision.fire.handler.ChannelHandler;
import cn.foxtech.channel.hikvision.fire.handler.ManageHandler;
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

import java.util.HashMap;
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

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private ManageHandler manageHandler;


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
            Map<String, Object> register = (Map<String, Object>) config.getOrDefault("register", new HashMap<>());
            String channelName = (String) register.getOrDefault("channelName", "");
            String manufacturer = (String) register.getOrDefault("manufacturer", "");
            String deviceType = (String) register.getOrDefault("deviceType", "");
            String deviceName = (String) register.getOrDefault("deviceName", "");

            // 底层的分拆和身份识别handler
            SplitMessageHandler splitMessageHandler = new SplitMessageHandler();
            ServiceKeyHandler serviceKeyHandler = new ServiceKeyHandler();


            // 绑定关系
            ChannelHandler channelHandler = new ChannelHandler();
            channelHandler.setServiceKeyHandler(serviceKeyHandler);
            channelHandler.setChannelManager(this.channelManager);
            channelHandler.setLogger(this.channelProperties.isLogger());
            channelHandler.setConsole(this.console);
            channelHandler.setSessionHandler(this.sessionHandler);
            channelHandler.setManageHandler(this.manageHandler);

            // 绑定需要创建的通道和设备名称
            this.manageHandler.setChannelName(channelName);
            this.manageHandler.setManufacturer(manufacturer);
            this.manageHandler.setDeviceType(deviceType);
            this.manageHandler.setDeviceName(deviceName);

            // 创建一个Tcp Server实例
            NettyTcpServer.createServer(serverPort, splitMessageHandler, channelHandler);
        } catch (Exception e) {
            String message = "scanJarFile出现异常:" + e.getMessage();
            this.logger.error(message);
            this.console.error(message);
        }
    }
}
