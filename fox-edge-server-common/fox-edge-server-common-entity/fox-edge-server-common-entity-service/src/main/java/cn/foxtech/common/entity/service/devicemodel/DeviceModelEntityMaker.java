/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicemodel;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.entity.DeviceModelPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DevicePo是数据库格式的对象，DeviceEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceModelEntityMaker {
    /**
     * PO转Entity
     *
     * @param poList po列表
     * @return 对象列表
     */
    public static List<BaseEntity> makePoList2EntityList(List poList) {
        List<BaseEntity> resultList = new ArrayList<>();
        for (Object entity : poList) {
            DeviceModelPo po = (DeviceModelPo) entity;


            DeviceModelEntity result = DeviceModelEntityMaker.makePo2Entity(po);
            resultList.add(result);
        }

        return resultList;
    }

    public static DeviceModelPo makeEntity2Po(DeviceModelEntity entity) {
        DeviceModelPo result = new DeviceModelPo();
        result.bind(entity);

        result.setModelParam(JsonUtils.buildJsonWithoutException(entity.getModelParam()));
        result.setExtendParam(JsonUtils.buildJsonWithoutException(entity.getExtendParam()));
        return result;
    }

    public static DeviceModelEntity makePo2Entity(DeviceModelPo entity) {
        DeviceModelEntity result = new DeviceModelEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getModelParam(), Map.class);
            if (params != null) {
                result.setModelParam(params);
            } else {
                System.out.println("配置参数转换Json对象失败：" + entity.getModelName() + ":" + entity.getModelParam());
            }
        } catch (Exception e) {
            System.out.println("配置参数转换Json对象失败：" + entity.getModelName() + ":" + entity.getModelParam());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getExtendParam(), Map.class);
            if (params != null) {
                result.setExtendParam(params);
            } else {
                System.out.println("扩展参数转换Json对象失败：" + entity.getModelName() + ":" + entity.getExtendParam());
            }
        } catch (Exception e) {
            System.out.println("扩展参数转换Json对象失败：" + entity.getModelName() + ":" + entity.getExtendParam());
            e.printStackTrace();
        }

        return result;
    }
}
