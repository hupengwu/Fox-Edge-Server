package cn.foxtech.huawei.iotda.service.entity.property.gateway;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Service {
    private String service_id;
    private Map<String, Object> properties = new HashMap<>();
    private String event_time;
}
