/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_device")
public class DevicePo extends DeviceBase {

    /**
     * 配置参数：这是工作参数
     */
    private String deviceParam;

    /**
     * 扩展参数（非工作参数）：主要是一些备注信息，它并不参与fox-edge本身的工作
     */
    private String extendParam;

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.deviceParam);
        list.add(this.extendParam);


        return list;
    }
}
