/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.entity;

import cn.foxtech.device.protocol.v1.iec104.core.enums.AsduTypeIdEnum;
import lombok.Data;

/**
 * ASDU(应用服务数据单元)
 * 结构：    类型标识符 | 可变结构限定词 | 传送原因 | 公共地址 | 信息体
 * 长度（6+N）： 1          1             2        2        N
 */
@Data
public class AsduEntity {
    /**
     * 类型标识TI:1字节
     */
    private int typeId = AsduTypeIdEnum.generalCall.getValue();
    /**
     * 可变结构限定词:1字节
     */
    private VsqEntity vsq = new VsqEntity();
    /**
     * 传输原因:2字节
     */
    private CotEntity cot = new CotEntity();
    /**
     * 应用服务数据单元公共地址:2字节
     */
    private int commonAddress;

    /**
     * 信息体
     */
    private byte[] data = new byte[0];
}
