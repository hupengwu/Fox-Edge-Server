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

/**
 * 将离散的设备参数聚合成复合的设备参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ExtendConfigBase extends BaseEntity {
    /**
     * 模板名称
     */
    private String extendName;

    /**
     * 模板类型
     */
    private String extendType;


    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.extendName);

        return list;
    }


    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("extend_name", this.extendName);


        return queryWrapper;
    }


    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.extendType);

        return list;
    }

    public void bind(ExtendConfigBase other) {
        this.extendName = other.extendName;
        this.extendType = other.extendType;

        super.bind(other);
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.extendName = (String) map.get("extendName");
        this.extendType = (String) map.get("extendType");
    }
}
