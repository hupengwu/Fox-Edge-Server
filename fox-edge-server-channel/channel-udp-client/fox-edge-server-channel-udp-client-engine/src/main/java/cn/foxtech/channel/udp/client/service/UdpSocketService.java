/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.udp.client.service;

import cn.foxtech.channel.udp.client.entity.ChannelSocket;
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
import java.util.Map;

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
            Map<String,Object> param = (Map<String,Object>) requestVO.getSend();
            int timeout = requestVO.getTimeout();

            String host = (String)param.get("host");
            Integer port = (Integer)param.get("port");
            String data = (String)param.get("data");


            InetAddress remoteIp = InetAddress.getByName(host);
            Integer remotePort = port;


            // 格式转换
            byte[] send = HexUtils.hexStringToByteArray(data);

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
