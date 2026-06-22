/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicesequence;

import cn.foxtech.common.entity.entity.DeviceSequenceEntity;
import cn.foxtech.common.entity.entity.DeviceSequencePo;
import cn.foxtech.common.entity.entity.LogEntity;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceRecordPo是数据库格式的对象，DeviceRecordEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceSequenceEntityMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<DeviceSequenceEntity> makePoList2EntityList(List<DeviceSequencePo> poList) {
        List<DeviceSequenceEntity> deviceRecordList = new ArrayList<>();
        for (LogEntity entity : poList) {
            DeviceSequencePo po = (DeviceSequencePo) entity;

            DeviceSequenceEntity config = DeviceSequenceEntityMaker.makePo2Entity(po);
            deviceRecordList.add(config);
        }

        return deviceRecordList;
    }

    public static DeviceSequencePo makeEntity2Po(DeviceSequenceEntity entity) {
        DeviceSequencePo result = new DeviceSequencePo();
        result.setDeviceName(entity.getDeviceName());
        result.setMetricName(entity.getMetricName());
        result.setTimestamp(entity.getTimestamp());


        result.setId(entity.getId());
        result.setCreateTime(entity.getCreateTime());


        result.setDeviceValue(JsonUtils.buildJsonWithoutException(entity.getDeviceValue()));
        return result;
    }

    public static DeviceSequenceEntity makePo2Entity(DeviceSequencePo entity) {
        DeviceSequenceEntity result = new DeviceSequenceEntity();
        result.setDeviceName(entity.getDeviceName());
        result.setMetricName(entity.getMetricName());
        result.setTimestamp(entity.getTimestamp());

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
