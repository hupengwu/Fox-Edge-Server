package cn.foxtech.device.protocol.iec104.core.entity;

import lombok.Data;

/**
 * S帧结构的控制实体：面向控制阶段的确认
 */
@Data
public class SControlEntity extends ControlEntity{
    /**
     * 接收序列号
     */
    private short accept;
}
