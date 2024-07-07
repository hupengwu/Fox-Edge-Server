package cn.foxtech.huawei.iotda.service.entity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Service {
    private String service_id;
    private String service_type;
    private List<Map<String, Object>> properties = new ArrayList<>();
    private Object commands;
    private Object events;
    private String description;
    private String option;
}
