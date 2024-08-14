/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

/**
 * 配置实体：各服务需要读取预置的全局配置参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_config")
public class ConfigPo extends ConfigBase {

    /**
     * 数值
     */
    private String configValue;
    /**
     * 参数
     */
    private String configParam;

    /**
     * 获取业务值
     *
     * @return  对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.configValue);

        return list;
    }
}
