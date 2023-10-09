package cn.foxtech.device.protocol.v1.s7plc.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SnmpDataEntity {

    /**
     * 名称
     */
    private String name;
    /**
     * 单位
     */
    private String oid;
    /**
     * 数值格式
     */
    private String type;
}
