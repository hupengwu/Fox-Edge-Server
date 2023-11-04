package cn.foxtech.channel.tcp.listener.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.tcp.listener.entity.TcpListenerEntity;
import cn.foxtech.channel.tcp.listener.handler.ChannelHandler;
import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.netty.client.tcp.NettyTcpClientFactory;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.ChannelFuture;
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
    private ChannelProperties channelProperties;

    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private ClassManager classManager;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelManager channelManager;


    public void initialize() {
        // 读取配置参数
        this.localConfigService.initialize("serverConfig", "serverConfig.json");
        Map<String, Object> configs = this.localConfigService.getConfigs();

        // 记录启动参数，方便后面全局使用
        this.channelProperties.setLogger((Boolean) configs.getOrDefault("logger", false));

        // 装载拆包和识别设备特征的解码器jar包
        List<Map<String, Object>> decoderList = (List<Map<String, Object>>) configs.get("decoderList");
        this.loadJarFiles(decoderList);

        // 创建执行器：自动维护连接的管理线程
        this.createExecutor();
    }

    private void loadJarFiles(List<Map<String, Object>> decoderList) {
        try {
            File file = new File("");
            String absolutePath = file.getAbsolutePath();

            // 取出需要加载的文件名
            for (Map<String, Object> map : decoderList) {
                String fileName = (String) map.get("jarFile");
                if (MethodUtils.hasEmpty(fileName)) {
                    continue;
                }

                // 不同操作系统下的文件路径是不同的
                String filePath = FileNameUtils.getOsFilePath(absolutePath + fileName);

                // 装载器：装载jar包
                JarLoaderUtils.loadJar(filePath);
            }

            // 装载器：加载类信息
            Set<Class<?>> classSet = JarLoaderUtils.getClasses(RootLocation.class.getPackage().getName());
            if (classSet != null) {
                this.classManager.setClassSet(classSet);
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("loadJarFiles 出现异常:" + e.getMessage());
        }
    }

    public void createExecutor() {
        ServerInitializer initializer = this;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        initializer.execute();

                        Thread.sleep(6 * 1000);
                    } catch (Exception e) {
                    }
                }

            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    private void execute() {
        for (String key : this.channelService.getChannelName2Entity().keySet()) {
            TcpListenerEntity entity = this.channelService.getChannelName2Entity().get(key);

            // 检查：南向通道是否建立，如果没有建立，那么重新发起连接
            if (this.channelManager.getContext(entity.getSocketAddress()) == null) {
                ChannelFuture channelFuture = this.connectRemote(entity.getRemoteHost(), entity.getRemotePort(), entity.getChannelHandler());
                entity.setChannelFuture(channelFuture);
            }
        }
    }

    private ChannelFuture connectRemote(String remoteHost, int remotePort, ChannelHandler channelHandler) {
        NettyTcpClientFactory factory = NettyTcpClientFactory.getInstance();
        factory.getChannelInitializer().setChannelHandler(channelHandler);
        factory.getChannelInitializer().setSplitMessageHandler(channelHandler.getSplitMessageHandler());

        return factory.createClient(remoteHost, remotePort);
    }

}
