/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import cn.foxtech.common.utils.number.NumberUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceObjectValue {
    /**
     * 数字
     */
    private Object value;
    /**
     * 更新时间
     */
    private Long time;

    public void bind(Map<String, Object> map) {
        this.value = map.get("value");
        this.time = NumberUtils.makeLong(map.get("time"));
    }
}
