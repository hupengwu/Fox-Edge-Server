package cn.foxtech.channel.tcp.server.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.tcp.server.entity.TcpClientSocket;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    /**
     * 查询设备数据:增加同步锁，避免并发访问带来的多线程异常。
     * @param ctx 上下文
     * @param requestVO 请求报文
     * @return 响应报文
     * @throws ServiceException 异常信息
     */
    public synchronized ChannelRespondVO execute(ChannelHandlerContext ctx, ChannelRequestVO requestVO) throws ServiceException {
        String name = requestVO.getName();
        String sendData = (String) requestVO.getSend();
        int timeout = requestVO.getTimeout();

        // 检查：数据是否为空
        if (sendData == null || sendData.isEmpty()) {
            throw new ServiceException("发送数据不能为空");
        }

        // 地址格式转换
        String[] host = name.split(":");
        if (host.length != 2) {
            throw new ServiceException("必须为IP:PORT格式！");
        }

        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);

        // 发送数据
        ctx.writeAndFlush(send);

//        // 接收数据
//        byte[] data = new byte[4096];
//        int recvLen = tcpClientSocket.recvData(data, timeout);
//        if (recvLen < 0) {
//            throw new ServiceException("接收socket数据返回失败！");
//        }
//
//        // 截取数据
//        byte[] recv = Arrays.copyOfRange(data, 0, recvLen);
//
//        // 格式转换
//        String hexString = HexUtils.byteArrayToHexString(recv, true);

        ChannelRespondVO respondVO = new ChannelRespondVO();
        respondVO.bindBaseVO(requestVO);
        respondVO.setRecv("");
        return respondVO;
    }
}
