package cn.foxtech.channel.serialport.entity;

import cn.foxtech.common.utils.serialport.AsyncExecutor;
import cn.foxtech.common.utils.serialport.ISerialPort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SerialChannelEntity {
    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 配置参数
     */
    private SerialConfigEntity config;

    /**
     * 串口
     */
    private ISerialPort serialPort;

    /**
     * 异步执行器：全双工模式
     */
    private AsyncExecutor asyncExecutor;

    /**
     * 激活时间
     */
    private Long activeTime = 0L;
}
