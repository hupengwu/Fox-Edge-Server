package cn.foxtech.channel.tcp.server.engine;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.socket.core.notify.OperateEntityKeyNotify;
import cn.foxtech.channel.socket.core.notify.OperateEntitySplitNotify;
import cn.foxtech.channel.socket.core.script.ScriptEngineService;
import cn.foxtech.channel.socket.core.service.ChannelManager;
import cn.foxtech.channel.tcp.server.handler.ChannelHandler;
import cn.foxtech.channel.tcp.server.service.ReportService;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import cn.foxtech.common.utils.netty.server.tcp.NettyTcpServer;
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
    private ReportService reportService;

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ScriptEngineService scriptEngineService;

    public void startJspEngine(Integer serverPort, Map<String, Object> engine) {
        try {
            Map<String, Object> keyHandler = (Map<String, Object>) engine.getOrDefault("keyHandler", new HashMap<>());
            Map<String, Object> splitHandler = (Map<String, Object>) engine.getOrDefault("splitHandler", new HashMap<>());
            String returnText = (String) engine.getOrDefault("returnText", "");

            OperateEntity find = new OperateEntity();
            find.setManufacturer((String) keyHandler.get("manufacturer"));
            find.setDeviceType((String) keyHandler.get("deviceType"));
            find.setOperateName("keyHandler");

            // 获得操作实体：身份识别
            OperateEntity keyHandlerEntity = this.entityManageService.getEntity(find.makeServiceKey(), OperateEntity.class);
            if (keyHandlerEntity == null) {
                throw new ServiceException("获得身份识别keyHandler的操作方法出错：manufacturer, deviceType" + find.makeServiceKey());
            }

            find = new OperateEntity();
            find.setManufacturer((String) splitHandler.get("manufacturer"));
            find.setDeviceType((String) splitHandler.get("deviceType"));
            find.setOperateName("splitHandler");

            // 获得操作实体：报文分拆
            OperateEntity splitHandlerEntity = this.entityManageService.getEntity(find.makeServiceKey(), OperateEntity.class);
            if (splitHandlerEntity == null) {
                throw new ServiceException("获得报文分拆splitHandler的操作方法出错：manufacturer, deviceType" + find.makeServiceKey());
            }

            ConsumerRedisService consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(OperateEntity.class);


            // 绑定动态通知
            OperateEntitySplitNotify splitNotify = new OperateEntitySplitNotify();
            splitNotify.setScriptEngineService(this.scriptEngineService);
            splitNotify.setConsoleService(this.console);
            splitNotify.setOperateEntity(splitHandlerEntity);
            splitNotify.setFormat((String) splitHandler.getOrDefault("format", "HEX"));
            splitNotify.reset();
            consumerRedisService.bindEntityNotify(splitNotify);

            // 绑定动态通知
            OperateEntityKeyNotify keyNotify = new OperateEntityKeyNotify();
            keyNotify.setScriptEngineService(this.scriptEngineService);
            keyNotify.setConsoleService(this.console);
            keyNotify.setOperateEntity(keyHandlerEntity);
            keyNotify.setFormat((String) splitHandler.getOrDefault("format", "HEX"));
            keyNotify.reset();
            consumerRedisService.bindEntityNotify(keyNotify);


            ChannelHandler channelHandler = new ChannelHandler();
            channelHandler.setServiceKeyHandler(keyNotify.getServiceKeyHandler());
            channelHandler.setChannelManager(this.channelManager);
            channelHandler.setReportService(this.reportService);
            channelHandler.setLogger(this.channelProperties.isLogger());
            channelHandler.setReturnText(returnText);

            // 创建一个Tcp Server实例
            NettyTcpServer.createServer(serverPort, splitNotify.getSplitMessageHandler(), channelHandler);
        } catch (Exception e) {
            String message = "startJspEngine出现异常:" + e.getMessage();
            this.logger.info(message);
            this.console.info(message);
        }
    }
}
