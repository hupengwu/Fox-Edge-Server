package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_sequence")
public class DeviceSequencePo extends LogEntity {
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
     * 设备数值
     */
    private String deviceValue;
}
