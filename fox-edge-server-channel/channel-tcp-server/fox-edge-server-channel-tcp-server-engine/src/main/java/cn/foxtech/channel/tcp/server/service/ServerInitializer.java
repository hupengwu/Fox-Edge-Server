/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.tcp.server.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.tcp.server.engine.JarEngine;
import cn.foxtech.channel.tcp.server.engine.JspEngine;
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


    public void initialize() {
        // 读取配置参数
        this.localConfigService.initialize();
        Map<String, Object> configs = this.localConfigService.getConfig();

        // 记录启动参数，方便后面全局使用
        this.channelProperties.setLogger((Boolean) configs.getOrDefault("logger", false));

        // 装载jar包
        this.jarEngine.loadJarFiles(configs);

        // 启动多个服务器
        this.startTcpServer(configs);
    }

    /**
     * 启动一个TCP Server
     *
     * @param config 配置参数项目
     */
    private void startTcpServer(Map<String, Object> config) {

        List<Map<String, Object>> servers = (List<Map<String, Object>>) config.get("servers");
        for (Map<String, Object> server : servers) {
            Integer serverPort = 0;
            try {
                serverPort = (Integer) server.get("serverPort");
                Map<String, Object> engine = (Map<String, Object>) server.getOrDefault("engine", new HashMap<>());
                String engineType = (String) engine.get("engineType");

                // 检测配置参数
                if (MethodUtils.hasEmpty(engineType, serverPort)) {
                    throw new ServiceException("全局配置参数不能为空：engineType, serverPort");
                }

                if ("Java".equals(engineType)) {
                    this.jarEngine.startEngine(serverPort, engine);
                } else if ("JavaScript".equals(engineType)) {
                    this.jspEngine.startEngine(serverPort, engine);
                } else {
                    throw new ServiceException("不支持的engineType：" + engineType);
                }

                String message = "启动服务端口成功:" + serverPort;
                this.logger.info(message);
                this.console.info(message);

            } catch (Exception e) {
                String message = "启动服务端口失败:" + serverPort + "。 失败描述：" + e.getMessage();
                this.logger.error(message);
                this.console.error(message);
            }
        }
    }
}
