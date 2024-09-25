/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceBase extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型名
     */
    private String deviceType;

    /**
     * 设备厂商名称
     */
    private String manufacturer;


    /**
     * 通道类型名：连接设备的服务名，比如"serialport"
     */
    private String channelType;

    /**
     * 通道名称：连接设备的串口名，比如"COM1"
     */
    private String channelName;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceName);

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_name", this.deviceName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return  对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceType);
        list.add(this.manufacturer);
        list.add(this.channelName);
        list.add(this.channelType);
        return list;
    }

    public void bind(DeviceBase other) {
        super.bind(other);

        this.deviceName = other.deviceName;
        this.deviceType = other.deviceType;
        this.manufacturer = other.manufacturer;
        this.channelType = other.channelType;
        this.channelName = other.channelName;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.deviceName = (String) map.get("deviceName");
        this.deviceType = (String) map.get("deviceType");
        this.manufacturer = (String) map.get("manufacturer");
        this.channelType = (String) map.get("channelType");
        this.channelName = (String) map.get("channelName");
    }
}
