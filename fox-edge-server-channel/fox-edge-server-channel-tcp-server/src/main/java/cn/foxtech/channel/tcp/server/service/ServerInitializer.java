package cn.foxtech.channel.tcp.server.service;

import cn.foxtech.channel.common.service.ConfigManageService;
import cn.foxtech.channel.tcp.server.handler.SocketChannelHandler;
import cn.foxtech.common.entity.manager.EntityConfigManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.netty.server.nettty.BootNettyServer;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 启动TCP服务器的异步线程
 */
@Component
public class ServerInitializer {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityConfigManager entityConfigManager;

    @Autowired
    private ConfigManageService configManageService;

    @Autowired
    private SocketChannelHandler channelHandler;

    @Autowired
    private SocketChannelHandler socketChannelHandler;

    private SplitMessageHandler splitHandlerInstance;
    private ServiceKeyHandler serviceKeyHandler;


    public void initialize() {
        // 读取配置参数
        Map<String, Object> configs = this.configManageService.loadInitConfig("serverConfig", "serverConfig.json");

        // 扫描jar文件
        this.scanJarFile(configs);

        // 启动服务
        this.schedule(configs);
    }

    /**
     * 扫描解码器
     */
    public void scanJarFile(Map<String, Object> configs) {
        try {
            List<Map<String, Object>> configList = (List<Map<String, Object>>) configs.get("decoder");
            String splitHandler = (String) configs.get("splitHandler");
            String keyHandler = (String) configs.get("keyHandler");


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

            // 取出splitHandler的java类
            Class splitHandlerClass = this.getSplitHandler(classSet, splitHandler);
            if (splitHandlerClass == null) {
                this.logger.error("找不到splitHandler对应的JAVA类：" + splitHandler);
                return;
            }

            // 取出keyHandler的java类
            Class keyHandlerClass = this.getKeyHandler(classSet, keyHandler);
            if (keyHandlerClass == null) {
                this.logger.error("找不到keyHandler对应的JAVA类：" + splitHandler);
                return;
            }

            // 实例化一个SplitMessageHandler对象
            this.splitHandlerInstance = (SplitMessageHandler) splitHandlerClass.newInstance();
            this.serviceKeyHandler = (ServiceKeyHandler) keyHandlerClass.newInstance();

            // 绑定关系
            this.socketChannelHandler.setServiceKeyHandler(serviceKeyHandler);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("scanJarFile出现异常:" + e.getMessage());
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

    private void schedule(Map<String, Object> configs) {
        Integer serverPort = (Integer) configs.get("serverPort");

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    BootNettyServer server = new BootNettyServer();
                    server.getChannelInitializer().setSplitMessageHandler(splitHandlerInstance);
                    server.getChannelInitializer().setChannelHandler(channelHandler);
                    server.bind(serverPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }
}
