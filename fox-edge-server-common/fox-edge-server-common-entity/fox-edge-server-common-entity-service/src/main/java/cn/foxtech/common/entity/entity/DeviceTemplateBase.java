/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import cn.foxtech.common.entity.constant.DeviceTemplateVOFieldConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceTemplateBase extends BaseEntity {
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板名称
     */
    private String templateType;

    /**
     * 设备厂商
     */
    private String manufacturer;
    /**
     * 设备类型名
     */
    private String deviceType;
    /**
     * 子集名称
     */
    private String subsetName;

    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.manufacturer);
        list.add(this.deviceType);
        list.add(this.subsetName);
        list.add(this.templateType);
        list.add(this.templateName);

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manufacturer", this.manufacturer);
        queryWrapper.eq("device_type", this.deviceType);
        queryWrapper.eq("subset_name", this.subsetName);
        queryWrapper.eq("template_name", this.templateName);
        queryWrapper.eq("template_type", this.templateType);

        return queryWrapper;
    }

    /**
     * 获取业务值
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceType);
        list.add(this.manufacturer);
        list.add(this.subsetName);
        list.add(this.templateType);
        list.add(this.templateName);
        return list;
    }

    public void bind(DeviceTemplateBase other) {
        this.templateName = other.templateName;
        this.deviceType = other.deviceType;
        this.subsetName = other.subsetName;
        this.manufacturer = other.manufacturer;
        this.templateType = other.templateType;

        super.bind(other);
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.templateName = (String) map.get(DeviceTemplateVOFieldConstant.field_template_name);
        this.templateType = (String) map.get(DeviceTemplateVOFieldConstant.field_template_type);
        this.manufacturer = (String) map.get(DeviceTemplateVOFieldConstant.field_manufacturer);
        this.deviceType = (String) map.get(DeviceTemplateVOFieldConstant.field_device_type);
        this.subsetName = (String) map.get(DeviceTemplateVOFieldConstant.field_subset_name);
    }
}
