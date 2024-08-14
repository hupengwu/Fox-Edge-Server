/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class IotDeviceModelEntity extends IotDeviceModelBase {
    /**
     * 业务参数（描述类的信息）：各厂家的各自定义，差异很大，所以用可变的json
     */
    private Map<String, Object> serviceParam = new HashMap<>();
    /**
     * 模型结构（模型信息）:模型结构的描述信息
     */
    private Map<String, Object> modelSchema = new HashMap<>();

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = super.makeServiceKeyList();

        return list;
    }


    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.serviceParam);
        list.add(this.modelSchema);

        return list;
    }

    public void bind(IotDeviceModelEntity other) {
        super.bind(other);

        this.serviceParam = other.serviceParam;
        this.modelSchema = other.modelSchema;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.serviceParam = ((Map<String, Object>) map.getOrDefault("serviceParam", new HashMap<>()));
        this.modelSchema = ((Map<String, Object>) map.getOrDefault("modelSchema", new HashMap<>()));
    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            IotDeviceModelEntity entity = new IotDeviceModelEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}