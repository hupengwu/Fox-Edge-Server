package cn.foxtech.huawei.iotda.service.entity.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceStatus {
    private String device_id;
    private String status;
}
