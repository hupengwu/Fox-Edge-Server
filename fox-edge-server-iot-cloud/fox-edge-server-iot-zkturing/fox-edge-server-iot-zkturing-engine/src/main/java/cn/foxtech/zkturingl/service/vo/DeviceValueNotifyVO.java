package cn.foxtech.zkturingl.service.vo;

import cn.foxtech.common.entity.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceValueNotifyVO {
    /**
     * 执行的动作
     */
    private String method;
    /**
     * 变更的内容
     */
    private BaseEntity entity;
}
