package cn.foxtech.channel.tcp.server.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Component
public class PublishService {
    public void publish(ChannelHandlerContext ctx, InetSocketAddress skt, ChannelRequestVO requestVO) throws ServiceException {
        String sendData = (String) requestVO.getSend();

        // 检查：数据是否为空
        if (sendData == null || sendData.isEmpty()) {
            throw new ServiceException("发送数据不能为空");
        }

        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);


        // 发送数据
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(send), skt));
    }
}
