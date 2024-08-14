/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 设备的配置命令为：cmd=xxxyyy\r\n的格式，xxx是大写的命令字，yyy是命令字规定的数值格式
 * 例如：
 * cmd=getstatus\r\n
 * cmd=setofch0x\r\n
 * cmd=setch0fdelay0008s\r\n
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
