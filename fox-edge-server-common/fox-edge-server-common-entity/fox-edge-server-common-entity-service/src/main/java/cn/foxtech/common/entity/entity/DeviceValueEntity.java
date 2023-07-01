package cn.foxtech.common.entity.entity;


import cn.foxtech.common.entity.constant.DeviceValueVOFieldConstant;
import cn.foxtech.common.utils.number.NumberUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceValueEntity extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型名
     */
    private String deviceType;

    /**
     * 配置集合
     */
    private Map<String, DeviceObjectValue> params = new HashMap<>();

    public static DeviceValueEntity buildValueEntity(Map<String, Object> deviceValueMap) {
        if (deviceValueMap == null) {
            return null;
        }

        Long id = NumberUtils.makeLong(deviceValueMap.get(DeviceValueVOFieldConstant.field_id));
        Long createTime = NumberUtils.makeLong(deviceValueMap.get(DeviceValueVOFieldConstant.field_create_time));
        Long updateTime = NumberUtils.makeLong(deviceValueMap.get(DeviceValueVOFieldConstant.field_update_time));

        String deviceName = (String) deviceValueMap.get(DeviceValueVOFieldConstant.field_device_name);
        String deviceType = (String) deviceValueMap.get(DeviceValueVOFieldConstant.field_device_type);

        Map<String, DeviceObjectValue> params = new HashMap<>();
        Map<String, Map<String, Object>> values = (Map<String, Map<String, Object>>) deviceValueMap.getOrDefault(DeviceValueVOFieldConstant.field_params, new HashMap<>());
        for (String key : values.keySet()) {
            DeviceObjectValue objectValue = new DeviceObjectValue();
            Map<String, Object> map = values.get(key);
            if (map == null) {
                params.put(key, null);
                continue;
            }

            objectValue.setValue(map.get(DeviceValueVOFieldConstant.field_value_value));
            objectValue.setTime(NumberUtils.makeLong(map.get(DeviceValueVOFieldConstant.field_value_time)));

            params.put(key, objectValue);
        }

        DeviceValueEntity valueEntity = new DeviceValueEntity();
        valueEntity.setId(id);
        valueEntity.setCreateTime(createTime);
        valueEntity.setUpdateTime(updateTime);
        valueEntity.setDeviceName(deviceName);
        valueEntity.setDeviceType(deviceType);
        valueEntity.setParams(params);


        return valueEntity;
    }

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceName);

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_name", this.deviceName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.params);
        list.add(this.deviceType);
        return list;
    }

    public void bind(DeviceValueEntity other) {
        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());

        this.deviceName = other.deviceName;
        this.deviceType = other.deviceType;
        this.params = other.params;
    }
}
