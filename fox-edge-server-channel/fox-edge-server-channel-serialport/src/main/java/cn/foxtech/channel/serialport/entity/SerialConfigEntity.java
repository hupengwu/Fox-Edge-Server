package cn.foxtech.channel.serialport.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SerialConfigEntity {
    /**
     * 串口名：
     * Linux基本上为tty这样的格式，例如ttyS1，可以在Linux执行命令ls -al /dev/tty*查看
     * Windows基本为COM这样的格式，可以在操作系统的设备管理面板上查看
     */
    private String serialName;
    /**
     * 波特率
     */
    private Integer baudRate;
    /**
     * bit位
     */
    private Integer databits;
    /**
     * 校验位
     */
    private String parity;
    /**
     * 停止位
     */
    private Integer stopbits;
    /**
     * 每个字节之间的时间间隔：解决某些设备，发送断断续续的情况，此时直接使用操作系统的状态感知，会导致接收不全的状况
     */
    private Integer minPackInterval;
    /**
     * 是否为全双工模式
     */
    private Boolean fullDuplex;

}
