package cn.foxtech.channel.udp.server.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.udp.server.engine.JarEngine;
import cn.foxtech.channel.udp.server.engine.JspEngine;
import cn.foxtech.channel.udp.server.handler.ManageHandler;
import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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
    private JarEngine jarEngine;

    @Autowired
    private JspEngine jspEngine;

    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private ManageHandler manageHandler;

    @Autowired
    private ChannelService channelService;


    public void initialize() {
        // 读取配置参数
        this.localConfigService.initialize();
        Map<String, Object> configs = this.localConfigService.getConfig();

        // 记录启动参数，方便后面全局使用
        this.channelProperties.setLogger((Boolean) configs.getOrDefault("logger", false));

        // 设置生命周期
        Long lifeCycle = Long.parseLong(configs.getOrDefault("lifeCycle", 2600).toString());
        this.manageHandler.setLifeCycle(lifeCycle);
        this.channelService.setLifeCycle(lifeCycle);

        // 装载jar包
        this.jarEngine.loadJarFiles(configs);

        // 启动多个服务器
        this.startUdpServer(configs);
    }

    /**
     * 启动一个TCP Server
     *
     * @param config 配置参数项目
     */
    private void startUdpServer(Map<String, Object> config) {
        List<Map<String, Object>> servers = (List<Map<String, Object>>) config.get("servers");
        for (Map<String, Object> server : servers) {
            try {
                Integer serverPort = (Integer) server.get("serverPort");
                Map<String, Object> engine = (Map<String, Object>) server.getOrDefault("engine", new HashMap<>());
                String engineType = (String) engine.get("engineType");

                // 检测配置参数
                if (MethodUtils.hasEmpty(engineType, serverPort)) {
                    throw new ServiceException("全局配置参数不能为空：engineType, serverPort");
                }

                if (engineType.equals("Java")) {
                    this.jarEngine.startJarEngine(serverPort, engine);
                }
                if (engineType.equals("JavaScript")) {
                    this.jspEngine.startJspEngine(serverPort, engine);
                }

                String message = "启动服务端口:" + serverPort;
                this.logger.info(message);
                this.console.info(message);

            } catch (Exception e) {
                String message = "scanJarFile出现异常:" + e.getMessage();
                this.logger.error(message);
                this.console.error(message);
            }
        }
    }
}
