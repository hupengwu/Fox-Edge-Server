/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateMonitorTaskBase extends BaseEntity {
    /**
     * 设备名称
     */
    private String templateName;

    /**
     * 设备类型名
     */
    private String deviceType;

    /**
     * 设备厂商
     */
    private String manufacturer;

    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
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
        queryWrapper.eq("template_name", this.templateName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceType);
        list.add(this.manufacturer);
        return list;
    }

    public void bind(OperateMonitorTaskBase other) {
        this.templateName = other.templateName;
        this.deviceType = other.deviceType;
        this.manufacturer = other.manufacturer;

        super.bind(other);
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.templateName = (String) map.get("templateName");
        this.deviceType = (String) map.get("deviceType");
        this.manufacturer = (String) map.get("manufacturer");
    }
}
