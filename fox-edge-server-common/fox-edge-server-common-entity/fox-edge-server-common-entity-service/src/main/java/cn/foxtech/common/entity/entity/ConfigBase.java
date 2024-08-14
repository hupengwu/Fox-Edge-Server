/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import cn.foxtech.common.utils.number.NumberUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 配置实体：各服务需要读取预置的全局配置参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ConfigBase extends BaseEntity {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 描述
     */
    private String remark;


    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.serviceName);
        list.add(this.serviceType);
        list.add(this.configName);

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_name", this.serviceName);
        queryWrapper.eq("service_type", this.serviceType);
        queryWrapper.eq("config_name", this.configName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        return list;
    }

    public void bind(ConfigBase other) {
        super.bind(other);

        this.serviceName = other.serviceName;
        this.serviceType = other.serviceType;
        this.configName = other.configName;
        this.remark = other.remark;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.serviceName = (String) map.get("serviceName");
        this.serviceType = (String) map.get("serviceType");
        this.configName = (String) map.get("configName");
        this.remark = (String) map.get("remark");
    }
}
