package cn.foxtech.huawei.iotda.service.entity.property.subdev;

import cn.foxtech.huawei.iotda.service.entity.property.Service;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PropertyReport {
    private String device_id;
    private List<Service> services = new ArrayList<>();
}
