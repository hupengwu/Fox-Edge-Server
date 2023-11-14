package cn.foxtech.channel.tcp.server.engine;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.socket.core.service.ChannelManager;
import cn.foxtech.channel.tcp.server.handler.ChannelHandler;
import cn.foxtech.channel.tcp.server.service.ReportService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.netty.server.tcp.NettyTcpServer;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.core.exception.ServiceException;
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

/**
 * Java的静态Jar包引擎
 */
@Component
public class JarEngine {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService consoleService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private ChannelManager channelManager;

    public void startJarEngine(Integer serverPort, Map<String, Object> engine) {
        try {
            String keyHandler = (String) engine.get("keyHandler");
            String splitHandler = (String) engine.get("splitHandler");

            if (MethodUtils.hasEmpty(keyHandler, splitHandler)) {
                throw new ServiceException("全局配置参数不能为空：keyHandler, splitHandler");
            }

            // 装载器：加载类信息
            Set<Class<?>> classSet = JarLoaderUtils.getClasses(RootLocation.class.getPackage().getName());

            // 取出splitHandler的java类
            Class splitHandlerClass = this.getSplitHandler(classSet, splitHandler);
            if (splitHandlerClass == null) {
                this.consoleService.error("找不到splitHandler对应的JAVA类：" + splitHandler);
                return;
            }

            // 取出keyHandler的java类
            Class keyHandlerClass = this.getKeyHandler(classSet, keyHandler);
            if (keyHandlerClass == null) {
                this.consoleService.error("找不到keyHandler对应的JAVA类：" + splitHandler);
                return;
            }

            // 实例化一个SplitMessageHandler对象
            SplitMessageHandler splitMessageHandler = (SplitMessageHandler) splitHandlerClass.newInstance();
            // 实例化一个serviceKeyHandler对象
            ServiceKeyHandler serviceKeyHandler = (ServiceKeyHandler) keyHandlerClass.newInstance();

            // 绑定关系
            ChannelHandler channelHandler = new ChannelHandler();
            channelHandler.setServiceKeyHandler(serviceKeyHandler);
            channelHandler.setChannelManager(this.channelManager);
            channelHandler.setReportService(this.reportService);
            channelHandler.setLogger(this.channelProperties.isLogger());

            // 创建一个Tcp Server实例
            NettyTcpServer.createServer(serverPort, splitMessageHandler, channelHandler);
        } catch (Exception e) {
            e.printStackTrace();
            this.consoleService.error("scanJarFile出现异常:" + e.getMessage());
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
