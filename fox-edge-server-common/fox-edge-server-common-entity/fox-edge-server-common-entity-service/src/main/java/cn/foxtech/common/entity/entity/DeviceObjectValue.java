package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceObjectValue {
    /**
     * 数字
     */
    private Object value;
    /**
     * 更新时间
     */
    private Long time;
}
