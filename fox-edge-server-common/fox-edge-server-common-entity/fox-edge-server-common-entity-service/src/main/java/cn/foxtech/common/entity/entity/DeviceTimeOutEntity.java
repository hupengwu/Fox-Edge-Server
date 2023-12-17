package cn.foxtech.common.entity.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 设备通信超时实体
 * 该实体在redis中固定保持最大100个最近通信不成功的设备，方便用户快速定位最近哪些设备通信不成功
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceTimeOutEntity extends DeviceStatusEntity {
    /**
     * 请求消息
     */
    private Object request;
}
