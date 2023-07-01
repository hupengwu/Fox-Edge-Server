package cn.foxtech.common.entity.entity;


import lombok.*;

/**
 * 配置实体：各服务需要读取预置的全局配置参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceHistoryEntity extends LogEntity {
    /**
     * 设备表的关联ID
     */
    private Long deviceId;
    /**
     * 收集到的数据：收集到的名称
     */
    private String objectName;

    /**
     * 收集到的数据：收集到的数据
     */
    private Object paramValue;
}
