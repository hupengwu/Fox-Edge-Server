/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicevaluerecord;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceValueRecordEntity;
import cn.foxtech.common.entity.entity.DeviceValueRecordPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceRecordPo是数据库格式的对象，DeviceRecordEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceValueRecordMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<DeviceValueRecordEntity> makePoList2EntityList(List<DeviceValueRecordPo> poList) {
        List<DeviceValueRecordEntity> deviceRecordList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            DeviceValueRecordPo po = (DeviceValueRecordPo) entity;

            DeviceValueRecordEntity config = DeviceValueRecordMaker.makePo2Entity(po);
            deviceRecordList.add(config);
        }

        return deviceRecordList;
    }

    public static DeviceValueRecordPo makeEntity2Po(DeviceValueRecordEntity entity) {
        DeviceValueRecordPo result = new DeviceValueRecordPo();
        result.setDeviceName(entity.getDeviceName());
        result.setDeviceType(entity.getDeviceType());
        result.setManufacturer(entity.getManufacturer());


        result.setId(entity.getId());
        result.setCreateTime(entity.getCreateTime());


        result.setDeviceValue(JsonUtils.buildJsonWithoutException(entity.getDeviceValue()));
        return result;
    }

    public static DeviceValueRecordEntity makePo2Entity(DeviceValueRecordPo entity) {
        DeviceValueRecordEntity result = new DeviceValueRecordEntity();
        result.setDeviceName(entity.getDeviceName());
        result.setDeviceType(entity.getDeviceType());
        result.setManufacturer(entity.getManufacturer());

        result.setId(entity.getId());
        result.setCreateTime(entity.getCreateTime());


        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getDeviceValue(), Map.class);
            if (params != null) {
                result.setDeviceValue(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getDeviceValue());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getDeviceValue());
            e.printStackTrace();
        }

        return result;
    }
}
