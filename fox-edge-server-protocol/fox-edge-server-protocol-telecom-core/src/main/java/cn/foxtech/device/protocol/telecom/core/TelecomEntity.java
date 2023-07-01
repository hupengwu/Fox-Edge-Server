package cn.foxtech.device.protocol.telecom.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Telecom的报文实体
 */
@Setter(value = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
public class TelecomEntity {
    /**
     * 版本
     */
    private byte ver = 0;
    /**
     * 地址
     */
    private byte addr = 0;
    /**
     * CID1
     */
    private byte CID1 = 0;
    /**
     * CID2
     */
    private byte CID2 = 0;
    /**
     * 数据域
     */
    private byte[] data = new byte[0];
}
