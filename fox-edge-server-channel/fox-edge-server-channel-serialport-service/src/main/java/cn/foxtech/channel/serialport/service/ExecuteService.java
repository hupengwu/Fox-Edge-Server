package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.common.utils.serialport.linux.entity.OutValue;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

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
            throw new ServiceException("串口不存在！");
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
        OutValue sendLen = new OutValue();
        if (!serialPort.sendData(send, sendLen)) {
            throw new ServiceException("串口发送数据失败！");
        }

        // 接收数据
        byte[] data = new byte[4096];
        OutValue recvLen = new OutValue();
        serialPort.recvData(data, timeout, recvLen);
        if (((int) recvLen.getObj()) == 0) {
            throw new ServiceException("串口在超时范围内，未接收到返回数据！");
        }

        // 截取数据
        byte[] recv = Arrays.copyOfRange(data, 0, (int) recvLen.getObj());

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
    public void publish(ISerialPort serialPort, ChannelRequestVO requestVO) throws ServiceException {
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
            throw new ServiceException("串口不存在！");
        }
        if (!serialPort.isOpen()) {
            throw new ServiceException("串口没有打开！");
        }


        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);


        // 清空串口上的发送/接收缓冲区
        serialPort.clearSendFlush();

        // 发送数据
        OutValue sendLen = new OutValue();
        if (!serialPort.sendData(send, sendLen)) {
            throw new ServiceException("串口发送数据失败！");
        }
    }
}
