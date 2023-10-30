package cn.foxtech.huawei.iotda.service.entity.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SubDevOnlineParas {
    private List<DeviceStatus> device_statuses = new ArrayList<>();
}
