package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelBaseVO;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.serialport.entity.SerialChannelEntity;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.serialport.AsyncExecutor;
import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    /**
     * 主动上报的topic
     */
    private final String reportTopic = RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_manager;
    /**
     * 属性信息
     */
    @Autowired
    private ChannelProperties channelProperties;

    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @return
     */
    public synchronized ChannelRespondVO execute(ISerialPort serialPort, ChannelRequestVO requestVO) throws ServiceException {
        String sendData = (String) requestVO.getSend();
        Integer timeout = requestVO.getTimeout();

        if (sendData == null || sendData.isEmpty()) {
            throw new ServiceException("发送数据不能为空！");
        }
        if (timeout == null) {
            throw new ServiceException("超时参数不能为空！");
        }
        if (timeout > 10 * 1000) {
            throw new ServiceException("超时参数不能大于10秒！");
        }

        // 检查串口
        if (serialPort == null) {
            throw new ServiceException("串口不存在或者未打开！");
        }
        if (!serialPort.isOpen()) {
            throw new ServiceException("串口没有打开！");
        }


        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);


        // 清空串口上的发送/接收缓冲区
        serialPort.clearSendFlush();
        serialPort.clearRecvFlush();

        // 发送数据
        serialPort.sendData(send);

        // 接收数据
        byte[] data = new byte[4096];
        int recvLen = serialPort.recvData(data, timeout);
        if (recvLen <= 0) {
            throw new ServiceException("串口在超时范围内，未接收到返回数据！");
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

    /**
     * 执行发布操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    public void publish(SerialChannelEntity channelEntity, ChannelRequestVO requestVO) throws ServiceException {
        String sendData = (String) requestVO.getSend();
        Integer timeout = requestVO.getTimeout();

        if (sendData == null || sendData.isEmpty()) {
            throw new ServiceException("发送数据不能为空！");
        }
        if (timeout == null) {
            throw new ServiceException("超时参数不能为空！");
        }
        if (timeout > 10 * 1000) {
            throw new ServiceException("超时参数不能大于10秒！");
        }

        if (!channelEntity.getConfig().getFullDuplex()) {
            throw new ServiceException("半双工模式，不允许进行单向操作:" + requestVO.getName());
        }

        // 检查串口
        if (channelEntity.getSerialPort() == null) {
            throw new ServiceException("串口不存在！");
        }
        if (!channelEntity.getSerialPort().isOpen()) {
            throw new ServiceException("串口没有打开！");
        }


        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);

        // 通过异步线程执行前期，发送数据
        channelEntity.getAsyncExecutor().waitWriteable(send);
    }

    public List<ChannelRespondVO> report(Map<String, SerialChannelEntity> channelEntityMap) throws ServiceException {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();
        for (String channelName : channelEntityMap.keySet()) {
            SerialChannelEntity channelEntity = channelEntityMap.get(channelName);
            if (channelEntity == null) {
                continue;
            }

            // 检测：串口是否打开
            if (channelEntity.getSerialPort() == null) {
                continue;
            }

            if (!channelEntity.getSerialPort().isOpen()) {
                continue;
            }

            // 检测：是否为全双工模式
            if (!channelEntity.getConfig().getFullDuplex()) {
                continue;
            }

            // 取得异步执行器
            AsyncExecutor asyncExecutor = channelEntity.getAsyncExecutor();
            if (asyncExecutor == null) {
                continue;
            }

            // 检测：是否有数据到达
            if (!asyncExecutor.isReadable()) {
                continue;
            }

            // 取出数据
            List<byte[]> list = asyncExecutor.waitReadable(100);

            for (byte[] data : list) {
                String hex = HexUtils.byteArrayToHexString(data);
                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.setName(channelName);
                respondVO.setRoute(this.reportTopic);
                respondVO.setMode(ChannelBaseVO.MODE_RECEIVE);
                respondVO.setType(this.channelProperties.getChannelType());
                respondVO.setRecv(hex);


                respondVOList.add(respondVO);
            }
        }

        return respondVOList;
    }
}