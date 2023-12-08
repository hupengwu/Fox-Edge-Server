package cn.foxtech.channel.serialport.entity;

import cn.foxtech.common.utils.serialport.AsyncExecutor;
import cn.foxtech.common.utils.serialport.ISerialPort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 全局的串口对象实体
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SerialPortEntity {
    /**
     * 通道名称
     */
    private String channelName;

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

    /**
     * 数据流缓存：XXX+[设备报文]+XXX，它涉及到在混杂中的内容翻找
     */
    private SerialStreamEntity streamEntity = new SerialStreamEntity();
}
