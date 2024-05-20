package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceValueExObjectValue {
    /**
     * 缓存队列的深度
     */
    private int cacheSize = 1;

    /**
     * 数值
     */
    private List<DeviceObjectValue> values = new ArrayList<>();
}
