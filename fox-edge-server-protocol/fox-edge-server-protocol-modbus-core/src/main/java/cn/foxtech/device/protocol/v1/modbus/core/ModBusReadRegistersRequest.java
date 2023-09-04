package cn.foxtech.device.protocol.v1.modbus.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 寄存器状态查询请求
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ModBusReadRegistersRequest {
    /**
     * 报文实体
     */
    private ModBusEntity entity = new ModBusEntity();

    /**
     * 地址偏移量
     */
    private int memAddr = 0;

    /**
     * 线圈数量
     */
    private int count = 0;
}
