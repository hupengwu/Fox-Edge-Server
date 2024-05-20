package cn.foxtech.value.ex.task.service.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DataObject {
    /**
     * 数据源的类型：必填参数
     * all：设备下的全体对象
     * object：设备下的对象列表
     */
    private String objectType = "object";
    /**
     * 对象类型：可填参数
     */
    private Set<String> objectName = new HashSet<>();

    public void bind(Map<String, Object> param) {
        this.objectType = (String) param.getOrDefault("objectType","");

        this.objectName.clear();
        this.objectName.addAll((Collection) param.getOrDefault("objectName",new ArrayList<>()));

    }
}
