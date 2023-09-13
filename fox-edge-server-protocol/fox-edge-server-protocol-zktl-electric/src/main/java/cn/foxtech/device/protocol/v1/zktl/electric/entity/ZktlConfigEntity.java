package cn.foxtech.device.protocol.v1.zktl.electric.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 设备的配置命令为：SETxxx=yyyAA的格式，xxx是大写的命令字，yyy是命令字规定的数值格式
 * 例如：
 * SETCH=255AA
 * SETUL=200VAA
 * SETINH=500mAAA
 */
@Getter
@Setter
public class ZktlConfigEntity {
    /**
     * 命令
     */
    private String cmd = "";
    /**
     * 数值
     */
    private String value = "";
}
