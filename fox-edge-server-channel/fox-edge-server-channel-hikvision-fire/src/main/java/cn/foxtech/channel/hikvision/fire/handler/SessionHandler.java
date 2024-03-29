package cn.foxtech.channel.hikvision.fire.handler;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.hikvision.fire.service.ReportService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.TcpPduEntity;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.enums.CmdType;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
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

    public void onMessage(ChannelHandlerContext ctx, String serviceKey, byte[] data) {
        try {
            TcpPduEntity pduEntity = TcpPduEntity.decodeEntity(data);
            int cmd = pduEntity.getCtrlEntity().getCmd();

            // 设备的确认：不需要进行应答
            if (CmdType.confirm.getCmd() != cmd && CmdType.deny.getCmd() != cmd && CmdType.respond.getCmd() != cmd) {
                // 构造一个应答报文
                TcpPduEntity rspEntity = new TcpPduEntity();
                // 复制源数据
                rspEntity.getCtrlEntity().bind(pduEntity.getCtrlEntity());
                // 原宿地址调换
                rspEntity.getCtrlEntity().swapAddr();
                // 修改为确认
                rspEntity.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
                // 打包成报文
                byte[] respond = TcpPduEntity.encodeEntity(rspEntity);

                // 返回给设备
                ctx.channel().writeAndFlush(respond);
            }


            if (this.channelProperties.isLogger()) {
                String message = "onMessage: " + ctx.channel().remoteAddress() + ": " + HexUtils.byteArrayToHexString(data);
                LOGGER.info(message);
                console.info(message);
            }

            // 上报数据
            this.report(serviceKey, pduEntity);
        } catch (Exception e) {
            if (this.channelProperties.isLogger()) {
                String message = "respond异常: " + ctx.channel().remoteAddress() + ": " + e.getMessage();
                LOGGER.info(message);
                console.info(message);
            }
        }
    }

    private void report(String serviceKey, TcpPduEntity pduEntity) throws JsonProcessingException {
        if (this.channelProperties.isLogger()) {
            String message = "report: " + JsonUtils.buildJson(pduEntity);
            LOGGER.info(message);
            console.info(message);
        }

        // 上报数据
        this.reportService.push(serviceKey, pduEntity);
    }
}
