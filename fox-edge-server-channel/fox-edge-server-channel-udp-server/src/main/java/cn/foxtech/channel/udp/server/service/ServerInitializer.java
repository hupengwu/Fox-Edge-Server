package cn.foxtech.channel.udp.server.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.udp.server.handler.ChannelHandler;
import cn.foxtech.common.entity.manager.ConfigManageService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.netty.server.udp.NettyUdpServer;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 启动TCP服务器的异步线程
 */
@Component
public class ServerInitializer {
    private static final Logger logger = Logger.getLogger(ServerInitializer.class);

    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private ConfigManageService configManageService;


    public void initialize() {
        // 读取配置参数
        this.configManageService.initialize("serverConfig", "serverConfig.json");
        Map<String, Object> configs = this.configManageService.getConfigParam("serverConfig");

        // 记录启动参数，方便后面全局使用
        this.channelProperties.setLogger((Boolean) configs.getOrDefault("logger", false));

        // 启动多个服务器
        this.startServers(configs);
    }

    /**
     * 扫描解码器
     *
     * @param configs 总配置参数
     */
    public void startServers(Map<String, Object> configs) {
        try {
            // 启动多个UDP 服务器
            List<Map<String, Object>> decoderList = (List<Map<String, Object>>) configs.get("decoderList");
            for (Map<String, Object> decoder : decoderList) {
                // 启动一个UDP Server
                this.startUdpServer(decoder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = "scanJarFile出现异常:" + e.getMessage();
            logger.error(message);
            this.console.error(message);
        }
    }

    /**
     * 启动一个TCP Server
     *
     * @param config 配置参数项目
     */
    private void startUdpServer(Map<String, Object> config) {
        try {
            List<Map<String, Object>> configList = (List<Map<String, Object>>) config.get("decoder");
            String splitHandler = (String) config.get("splitHandler");
            String keyHandler = (String) config.get("keyHandler");
            Integer serverPort = (Integer) config.get("serverPort");


            File file = new File("");
            String absolutePath = file.getAbsolutePath();

            // 取出需要加载的文件名
            for (Map<String, Object> map : configList) {
                String fileName = (String) map.get("jarFile");
                if (MethodUtils.hasEmpty(fileName)) {
                    continue;
                }

                // 装载器：装载jar包
                JarLoaderUtils.loadJar(absolutePath + fileName);
            }

            // 装载器：加载类信息
            Set<Class<?>> classSet = JarLoaderUtils.getClasses(RootLocation.class.getPackage().getName());


            // 取出keyHandler的java类
            Class keyHandlerClass = this.getKeyHandler(classSet, keyHandler);
            if (keyHandlerClass == null) {
                 String message = "找不到keyHandler对应的JAVA类：" + splitHandler;
                logger.error(message);
                this.console.error(message);
                return;
            }

            // 实例化一个serviceKeyHandler对象
            ServiceKeyHandler serviceKeyHandler = (ServiceKeyHandler) keyHandlerClass.newInstance();

            // 绑定关系
            ChannelHandler channelHandler = new ChannelHandler();
            channelHandler.setServiceKeyHandler(serviceKeyHandler);
            channelHandler.setChannelManager(this.channelManager);
            channelHandler.setReportService(this.reportService);
            channelHandler.setLogger(this.channelProperties.isLogger());

            // 创建一个Tcp Server实例
            NettyUdpServer.createServer(serverPort, channelHandler);
        } catch (Exception e) {
            e.printStackTrace();
            String message = "startUdpServer 出现异常:" + e.getMessage();
            logger.error(message);
            this.console.error(message);
        }
    }

    private Class getSplitHandler(Set<Class<?>> classSet, String className) {
        for (Class<?> aClass : classSet) {
            String name = aClass.getName();

            if (!SplitMessageHandler.class.isAssignableFrom(aClass)) {
                continue;
            }

            if (!name.equals(className)) {
                continue;
            }

            return aClass;
        }

        return null;
    }

    private Class getKeyHandler(Set<Class<?>> classSet, String className) {
        for (Class<?> aClass : classSet) {
            String name = aClass.getName();

            if (!ServiceKeyHandler.class.isAssignableFrom(aClass)) {
                continue;
            }

            if (!name.equals(className)) {
                continue;
            }

            return aClass;
        }

        return null;
    }

}
