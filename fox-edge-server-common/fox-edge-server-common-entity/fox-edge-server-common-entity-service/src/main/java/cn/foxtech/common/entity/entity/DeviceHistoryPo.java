/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 配置实体：各服务需要读取预置的全局配置参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_device_history")
public class DeviceHistoryPo extends LogEntity {
    /**
     * 对象表的关联ID
     */
    private Long deviceId;
    /**
     * 参数类型
     */
    private String objectName;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 参数值
     */
    private String paramValue;
}
