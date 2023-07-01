package cn.foxtech.channel.udpsocket.service;

import cn.foxtech.channel.udpsocket.entity.ChannelSocket;
import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

@Component
public class UdpSocketService extends ChannelServerAPI {
    @Autowired
    private ChannelSocket updServerSocket;

    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @return
     */
    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        try {
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

            InetAddress remoteIp = InetAddress.getByName(host[0]);
            Integer remotePort = Integer.parseInt(host[1]);


            // 格式转换
            byte[] send = HexUtils.hexStringToByteArray(sendData);

            // 打开socket

            if (!updServerSocket.isOpen()) {
                updServerSocket.open();
            }
            if (!updServerSocket.isOpen()) {
                throw new ServiceException("本地UDP端口打开失败:" + updServerSocket.getPort());
            }


            // 发送数据
            updServerSocket.trySend(remoteIp, remotePort, send);

            // 接收数据
            DatagramPacket packet = updServerSocket.tryRecvData(timeout);
            if (packet.getLength() <= 0) {
                throw new ServiceException("接收socket数据返回失败！");
            }


            // 提取数据
            byte[] recv = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());

            // 格式转换
            String hexString = HexUtils.byteArrayToHexString(recv, true);

            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(hexString);
            return respondVO;

        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
