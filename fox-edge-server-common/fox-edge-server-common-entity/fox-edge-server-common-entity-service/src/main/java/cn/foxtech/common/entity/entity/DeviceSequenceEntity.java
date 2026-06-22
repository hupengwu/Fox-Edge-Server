package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceSequenceEntity extends LogEntity {
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 指标名称
     */
    private String metricName;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 扩展参数
     */
    private Map<String, Object> deviceValue = new HashMap<>();
}
