/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_device_record")
public class DeviceRecordPo extends DeviceRecordBase {
    /**
     * JSON数据
     */
    private String recordData;

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.recordData);

        return list;
    }
}
