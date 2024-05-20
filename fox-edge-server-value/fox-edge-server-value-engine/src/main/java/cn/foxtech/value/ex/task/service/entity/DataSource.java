package cn.foxtech.value.ex.task.service.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DataSource {
    /**
     * 数据源的类型：必填参数，global/deviceType/deviceName
     * global：全体设备，此时指的是各种设备类型下的采样对象
     * deviceType：设备类型
     * deviceName：具体的某个设备
     */
    private String sourceType = "deviceType";
    /**
     * 设备厂商：必填参数
     */
    private String manufacturer;
    /**
     * 设备类型：必填参数
     */
    private String deviceType;
    /**
     * 设备名称：可填参数
     */
    private String deviceName;
    /**
     * 对象范围：必填参数
     */
    private DataObject dataObject = new DataObject();

    public void bind(Map<String, Object> param) {
        this.sourceType = (String) param.getOrDefault("sourceType","");
        this.manufacturer = (String) param.getOrDefault("manufacturer","");
        this.deviceType = (String) param.getOrDefault("deviceType","");
        this.deviceName = (String) param.getOrDefault("deviceName","");

        Map<String, Object> dataObject = (Map<String, Object>)  param.getOrDefault("dataObject",new HashMap<>());
        this.dataObject.bind(dataObject);

    }
}
