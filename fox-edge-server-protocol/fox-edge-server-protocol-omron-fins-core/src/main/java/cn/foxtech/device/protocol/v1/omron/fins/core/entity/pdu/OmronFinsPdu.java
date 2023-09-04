package cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 欧姆龙的一级报文结构
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OmronFinsPdu {
    // 命令字:
    /**
     * 命令字
     * 0x00：connect requst 连接请求数据帧
     * 0x01：connect Response，连接请求确认数据；
     * 0x02：data，数据传输；
     */
    private int command = 2;

    /**
     * 出错码
     */
    private int error = 0;


    /**
     * 数据区
     */
    private byte[] data = new byte[0];
}
