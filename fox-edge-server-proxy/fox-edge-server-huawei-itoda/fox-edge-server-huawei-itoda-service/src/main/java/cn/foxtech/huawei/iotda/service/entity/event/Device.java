package cn.foxtech.huawei.iotda.service.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Device {
    private String name;
    private String node_id;
    private String product_id;
    private String description;
}
