package cn.foxtech.huawei.iotda.service.entity.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Service {
    private String service_id;
    private String event_type;
    private String event_time;
    private String event_id;
    private Object paras;
}
