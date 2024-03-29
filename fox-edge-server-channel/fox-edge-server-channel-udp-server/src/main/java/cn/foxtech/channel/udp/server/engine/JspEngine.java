package cn.foxtech.channel.udp.server.engine;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.socket.core.notify.OperateEntityKeyNotify;
import cn.foxtech.channel.socket.core.script.ScriptEngineService;
import cn.foxtech.channel.udp.server.handler.ChannelHandler;
import cn.foxtech.channel.udp.server.handler.ManageHandler;
import cn.foxtech.channel.udp.server.handler.SessionHandler;
import cn.foxtech.channel.udp.server.service.ChannelManager;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import cn.foxtech.common.utils.netty.server.udp.NettyUdpServer;
import cn.foxtech.core.exception.ServiceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaScript的动态脚本引擎
 */
@Component
public class JspEngine {
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
    private EntityManageService entityManageService;

    @Autowired
    private ScriptEngineService scriptEngineService;

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private ManageHandler manageHandler;


    public void startJspEngine(Integer serverPort, Map<String, Object> engine) {
        try {
            Map<String, Object> keyHandler = (Map<String, Object>) engine.getOrDefault("keyHandler", new HashMap<>());
            String returnText = (String) engine.getOrDefault("returnText", "");
            Map<String, Object> register = (Map<String, Object>) engine.getOrDefault("register", new HashMap<>());
            String channelName = (String) register.getOrDefault("channelName", "");
            String manufacturer = (String) register.getOrDefault("manufacturer", "");
            String deviceType = (String) register.getOrDefault("deviceType", "");
            String deviceName = (String) register.getOrDefault("deviceName", "");

            OperateEntity find = new OperateEntity();
            find.setManufacturer((String) keyHandler.get("manufacturer"));
            find.setDeviceType((String) keyHandler.get("deviceType"));
            find.setOperateName("keyHandler");

            // 获得操作实体：身份识别
            OperateEntity keyHandlerEntity = this.entityManageService.getEntity(find.makeServiceKey(), OperateEntity.class);
            if (keyHandlerEntity == null) {
                throw new ServiceException("获得身份识别keyHandler的操作方法出错：manufacturer, deviceType" + find.makeServiceKey());
            }


            ConsumerRedisService consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(OperateEntity.class);


            // 绑定动态通知
            OperateEntityKeyNotify keyNotify = new OperateEntityKeyNotify();
            keyNotify.setScriptEngineService(this.scriptEngineService);
            keyNotify.setConsole(this.console);
            keyNotify.setOperateEntity(keyHandlerEntity);
            keyNotify.setFormat((String) keyHandler.getOrDefault("format", "HEX"));
            keyNotify.reset();
            consumerRedisService.bindEntityNotify(keyNotify);


            ChannelHandler channelHandler = new ChannelHandler();
            channelHandler.setServiceKeyHandler(keyNotify.getServiceKeyHandler());
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
            String message = "startJspEngine出现异常:" + e.getMessage();
            this.logger.info(message);
            this.console.info(message);
        }
    }
}
