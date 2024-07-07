package cn.foxtech.channel.udp.server.engine;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.udp.server.handler.ChannelHandler;
import cn.foxtech.channel.udp.server.handler.ManageHandler;
import cn.foxtech.channel.udp.server.handler.SessionHandler;
import cn.foxtech.channel.udp.server.service.ChannelManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.netty.server.udp.NettyUdpServer;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Java的静态Jar包引擎
 */
@Component
public class JarEngine {
    private final Logger logger = Logger.getLogger(this.getClass());
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ChannelManager channelManager;


    @Autowired
    private ChannelProperties channelProperties;


    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private ManageHandler manageHandler;

    public void startJarEngine(Integer serverPort, Map<String, Object> engine) {
        try {
            String keyHandler = (String) engine.get("keyHandler");
            String returnText = (String) engine.getOrDefault("returnText", "");
            Map<String, Object> register = (Map<String, Object>) engine.getOrDefault("register", new HashMap<>());
            String channelName = (String) register.getOrDefault("channelName", "");
            String manufacturer = (String) register.getOrDefault("manufacturer", "");
            String deviceType = (String) register.getOrDefault("deviceType", "");
            String deviceName = (String) register.getOrDefault("deviceName", "");

            if (MethodUtils.hasEmpty(keyHandler)) {
                throw new ServiceException("全局配置参数不能为空：keyHandler");
            }

            // 装载器：加载类信息
            Set<Class<?>> classSet = JarLoaderUtils.getClasses(RootLocation.class.getPackage().getName());

            // 取出keyHandler的java类
            Class keyHandlerClass = this.getKeyHandler(classSet, keyHandler);
            if (keyHandlerClass == null) {
                String message = "找不到keyHandler对应的JAVA类：" + keyHandler;
                this.console.error(message);
                this.logger.error(message);
                return;
            }

            // 实例化一个serviceKeyHandler对象
            ServiceKeyHandler serviceKeyHandler = (ServiceKeyHandler) keyHandlerClass.newInstance();

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

            this.sessionHandler.setReturnText(returnText);

            // 创建一个Tcp Server实例
            NettyUdpServer.createServer(serverPort, channelHandler);
        } catch (Exception e) {
            e.printStackTrace();

            String message = "scanJarFile出现异常：" + e.getMessage();
            this.console.error(message);
            this.logger.error(message);
        }
    }

    public void loadJarFiles(Map<String, Object> configs) {
        List<Map<String, Object>> jarFiles = (List<Map<String, Object>>) configs.get("jarFiles");
        if (jarFiles == null) {
            return;
        }

        File file = new File("");
        String absolutePath = file.getAbsolutePath();


        for (Map<String, Object> map : jarFiles) {
            String fileName = (String) map.get("jarFile");
            if (MethodUtils.hasEmpty(fileName)) {
                continue;
            }

            // 不同操作系统下的文件路径是不同的
            String filePath = FileNameUtils.getOsFilePath(absolutePath + fileName);

            // 装载器：装载jar包
            JarLoaderUtils.loadJar(filePath);
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
