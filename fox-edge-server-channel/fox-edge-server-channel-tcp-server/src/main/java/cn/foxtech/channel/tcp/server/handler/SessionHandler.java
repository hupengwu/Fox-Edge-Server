package cn.foxtech.channel.tcp.server.handler;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.tcp.server.service.ReportService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHandler.class);

    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ChannelProperties channelProperties;

    @Setter
    private String returnText;

    public void onMessage(ChannelHandlerContext ctx, String serviceKey, byte[] data) {
        try {
            if (this.channelProperties.isLogger()) {
                String message = "onMessage: " + ctx.channel().remoteAddress() + ": " + HexUtils.byteArrayToHexString(data);
                LOGGER.info(message);
                console.info(message);
            }

            // 上报数据
            if (MethodUtils.hasEmpty(this.returnText)) {
                // 保存PDU到接收缓存
                this.reportService.push(serviceKey, data);
            } else {
                String message = new String(data, this.returnText);
                this.reportService.push(serviceKey, message);
            }
        } catch (Exception e) {
            if (this.channelProperties.isLogger()) {
                String message = "respond异常: " + ctx.channel().remoteAddress() + ": " + e.getMessage();
                LOGGER.info(message);
                console.info(message);
            }
        }
    }
}
