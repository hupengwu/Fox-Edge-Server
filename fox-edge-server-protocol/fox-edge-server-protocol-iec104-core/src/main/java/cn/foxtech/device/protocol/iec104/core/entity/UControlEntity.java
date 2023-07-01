package cn.foxtech.device.protocol.iec104.core.entity;

import cn.foxtech.device.protocol.iec104.core.enums.UControlTypeEnum;
import lombok.Data;

/**
 * U帧结构的控制实体：U帧用于链路需要的建立/拆除/心跳
 */
@Data
public class UControlEntity extends ControlEntity {
    /**
     * 枚举
     */
    private UControlTypeEnum value;
}
