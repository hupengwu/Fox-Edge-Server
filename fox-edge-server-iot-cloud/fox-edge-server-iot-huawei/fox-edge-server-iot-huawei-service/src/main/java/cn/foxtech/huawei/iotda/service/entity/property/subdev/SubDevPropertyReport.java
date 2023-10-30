package cn.foxtech.huawei.iotda.service.entity.property.subdev;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SubDevPropertyReport {
    private List<Device> devices = new ArrayList<>();
}
