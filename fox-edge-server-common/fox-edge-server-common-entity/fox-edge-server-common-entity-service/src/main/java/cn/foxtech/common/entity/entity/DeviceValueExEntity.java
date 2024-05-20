package cn.foxtech.common.entity.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 触发值，跟设备值同构，但是它的生产者是触发器，而不是控制器
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceValueExEntity extends DeviceValueEntity {
}
