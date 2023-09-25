package cn.foxtech.channel.tcpsocket.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.tcpsocket.entity.TcpClientSocket;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @return
     */
    public synchronized ChannelRespondVO execute(TcpClientSocket tcpClientSocket, ChannelRequestVO requestVO) throws ServiceException {
        String name = requestVO.getName();
        String sendData = (String) requestVO.getSend();
        int timeout = requestVO.getTimeout();

        // 检查：数据是否为空
        if (sendData == null || sendData.isEmpty()) {
            throw new ServiceException("发送数据不能为空");
        }

        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);

        // 发送数据
        try {
            tcpClientSocket.sendData(send);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }

        // 接收数据
        byte[] data = new byte[4096];
        int recvLen = tcpClientSocket.recvData(data, timeout);
        if (recvLen < 0) {
            throw new ServiceException("接收socket数据返回失败！");
        }

        // 截取数据
        byte[] recv = Arrays.copyOfRange(data, 0, recvLen);

        // 格式转换
        String hexString = HexUtils.byteArrayToHexString(recv, true);

        ChannelRespondVO respondVO = new ChannelRespondVO();
        respondVO.bindBaseVO(requestVO);
        respondVO.setRecv(hexString);
        return respondVO;
    }
}
