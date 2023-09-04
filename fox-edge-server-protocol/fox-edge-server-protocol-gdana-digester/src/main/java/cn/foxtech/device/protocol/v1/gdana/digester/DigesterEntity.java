package cn.foxtech.device.protocol.v1.gdana.digester;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DigesterEntity {
    /**
     * 设备地址
     */
    private int addr = 0;

    /**
     * 子设备地址
     */
    private int subAddr = 0;

    /**
     * 命令字
     */
    private int func = 0;

    /**
     * 数据区
     */
    private byte[] data = new byte[0];
}
