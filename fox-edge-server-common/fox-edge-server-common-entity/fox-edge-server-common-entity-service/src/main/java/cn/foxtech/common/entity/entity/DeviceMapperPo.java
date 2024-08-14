/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_device_mapper")
public class DeviceMapperPo extends DeviceMapperBase {
    /**
     * 扩展信息
     */
    private String extendParam;

    /**
     * 获得init方法
     *
     * @return 初始化方法
     * @throws NoSuchMethodException 方法异常
     */
    public static Method getInitMethod() throws NoSuchMethodException {
        return DeviceMapperPo.class.getMethod("init", DeviceObjInfEntity.class);
    }

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        return super.makeServiceKeyList();
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        return super.makeWrapperKey();
    }

    /**
     * 获取业务值
     *
     * @return 数值成员列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.extendParam);
        return list;
    }

    public void bind(DeviceMapperPo other) {
        super.bind(other);

        this.extendParam = other.extendParam;
    }
}
