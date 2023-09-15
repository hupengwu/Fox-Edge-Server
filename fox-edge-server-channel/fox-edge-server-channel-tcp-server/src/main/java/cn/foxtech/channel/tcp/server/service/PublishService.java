package cn.foxtech.channel.tcp.server.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublishService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishService.class);

    @Autowired
    private ChannelProperties channelProperties;

    public void publish(ChannelHandlerContext ctx, ChannelRequestVO requestVO) throws ServiceException {
        String sendData = (String) requestVO.getSend();

        // 检查：数据是否为空
        if (sendData == null || sendData.isEmpty()) {
            throw new ServiceException("发送数据不能为空");
        }

        // 记录接收到的报文
        if (this.channelProperties.getLogger()) {
            LOGGER.info("channelRead: " + ctx.channel().remoteAddress() + ": " + sendData);
        }

        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);

        // 发送数据
        ctx.writeAndFlush(send);
    }
}
