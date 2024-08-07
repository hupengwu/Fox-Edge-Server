package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.ChannelStatusUpdater;
import cn.foxtech.channel.common.service.ConsoleLoggerPrinter;
import cn.foxtech.channel.domain.ChannelBaseVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.serialport.entity.SerialChannelEntity;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.serialport.AsyncExecutor;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReportService {
    /**
     * 属性信息
     */
    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private ConsoleLoggerPrinter printer;

    @Autowired
    private ChannelStatusUpdater statusUpdater;


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
            List<byte[]> list = asyncExecutor.readRecvList();

            for (byte[] data : list) {
                String hex = HexUtils.byteArrayToHexString(data);
                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.setName(channelName);
                respondVO.setMode(ChannelBaseVO.MODE_RECEIVE);
                respondVO.setType(this.channelProperties.getChannelType());
                respondVO.setRecv(hex);

                this.printer.printLogger(channelName, "接收", hex);

                // 通知第三方服务：接收到数据
                this.statusUpdater.updateParamStatus(respondVO.getName(), "recvTime", System.currentTimeMillis());


                respondVOList.add(respondVO);
            }
        }

        return respondVOList;
    }
}
