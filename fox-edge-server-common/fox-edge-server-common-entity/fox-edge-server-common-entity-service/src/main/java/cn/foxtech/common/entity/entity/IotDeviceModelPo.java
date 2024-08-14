/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_iot_device_model")
public class IotDeviceModelPo extends IotDeviceModelBase {
    /**
     * 业务参数（描述类的信息）：各厂家的各自定义，差异很大，所以用可变的json
     */
    private String serviceParam;

    /**
     * 模型结构（模型信息）:模型结构的描述信息
     */
    private String modelSchema;

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.serviceParam);
        list.add(this.modelSchema);


        return list;
    }
}
