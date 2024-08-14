/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

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
public class DeviceValueExCacheEntity extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型名
     */
    private String deviceType;

    /**
     * 设备参数
     */
    private String manufacturer;

    /**
     * 配置集合
     */
    private Map<String, DeviceValueExObjectValue> params = new HashMap<>();


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
     * @return 对象实体
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.params);
        list.add(this.deviceType);
        list.add(this.manufacturer);
        return list;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.deviceName = (String) map.get("deviceName");
        this.deviceType = (String) map.get("deviceType");
        this.manufacturer = (String) map.get("manufacturer");

        this.getParams().clear();
        Map<String, Object> values = (Map<String, Object>) map.get("params");
        for (String key : values.keySet()) {
            Map<String, Object> value = (Map<String, Object>) values.get(key);
            if (value == null) {
                continue;
            }

            DeviceValueExObjectValue objectValue = new DeviceValueExObjectValue();
            objectValue.bind(value);

            this.getParams().put(key, objectValue);
        }

    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            DeviceEntity entity = new DeviceEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
