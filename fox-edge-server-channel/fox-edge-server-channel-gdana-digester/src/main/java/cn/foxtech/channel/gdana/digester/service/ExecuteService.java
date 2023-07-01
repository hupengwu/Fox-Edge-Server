package cn.foxtech.channel.gdana.digester.service;

import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.protocol.gdana.digester.DigesterEntity;
import cn.foxtech.device.protocol.gdana.digester.DigesterProtocolFrame;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.common.utils.serialport.linux.entity.OutValue;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

        if (MethodUtils.hasEmpty(sendData, timeout)) {
            throw new ServiceException("参数不能为空:sendData,timeout");
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

        // 对设备进行操作
        String hexString = this.execute(serialPort, sendData, timeout);

        ChannelRespondVO respondVO = new ChannelRespondVO();
        respondVO.bindBaseVO(requestVO);
        respondVO.setRecv(hexString);
        return respondVO;
    }

    /**
     * 对可能出现的单包，双包进行处理，只返回最后一包
     * 设备可能返回一个拒绝报文：此时把拒绝报文放回给客户端
     * 设备可能返回一个确认报文和一个数据报文：此时把数据报文放回给客户端
     * 设备也可能出现其他情况，此时直接包解码异常
     *
     * @param serialPort
     * @param sendData
     * @param timeout
     * @return 返回设备的拒绝报文，或者设备的数据报文
     */
    private String execute(ISerialPort serialPort, String sendData, Integer timeout) {
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

        // 接收数据：可能是确认数据，也可能是直接应答的数据
        byte[] data = new byte[4096];
        OutValue recvLen = new OutValue();
        serialPort.recvData(data, 1000, recvLen);
        if (((int) recvLen.getObj()) == 0) {
            throw new ServiceException("串口在超时范围内，未接收到返回数据！");
        }

        // 截取数据
        byte[] recv = Arrays.copyOfRange(data, 0, (int) recvLen.getObj());

        // 解码：可能收到1个包，或者两个包
        List<DigesterEntity> entityList = DigesterProtocolFrame.decodeStickPack(recv);

        // 单包返回场景
        if (entityList.size() == 1) {
            DigesterEntity entity = entityList.get(0);

            // 确认报文场景：
            if ((entity.getFunc() == 0x90) && (entity.getData().length == 1)) {
                // 失败场景
                if (entity.getData()[0] == 0x00) {
                    throw new ServiceException("设备主动拒绝：fun=" + entity.getFunc() + ",data=0x00");
                }
                // 成功的场景
                if (entity.getData()[0] == 0x01) {
                    // 继续等待第二个数据包的抵达
                    serialPort.recvData(data, timeout, recvLen);
                    if (((int) recvLen.getObj()) == 0) {
                        throw new ServiceException("串口在超时范围内，未接收到返回数据！");
                    }
                    recv = Arrays.copyOfRange(data, 0, (int) recvLen.getObj());

                    return HexUtils.byteArrayToHexString(recv, true);
                }
            }
            // 广播读地址场景：
            if ((entity.getFunc() == 0x80) && (entity.getData().length == 9)) {
                return HexUtils.byteArrayToHexString(recv, true);
            }
        }
        // 粘包返回
        if (entityList.size() == 2) {
            // 将第二个包重新打包返回
            recv = DigesterProtocolFrame.encodePack(entityList.get(1));
            return HexUtils.byteArrayToHexString(recv, true);
        }

        throw new ServiceException("返回数据异常！");
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

        if (MethodUtils.hasEmpty(sendData, timeout)) {
            throw new ServiceException("参数不能为空:sendData,timeout");
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
