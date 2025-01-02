/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicetemplate;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceTemplateEntity;
import cn.foxtech.common.entity.entity.DeviceTemplatePo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperateTemplatePo是数据库格式的对象，OperateTemplateEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceTemplateMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity base : poList) {
            DeviceTemplatePo po = (DeviceTemplatePo) base;

            DeviceTemplateEntity entity = DeviceTemplateMaker.makePo2Entity(po);
            entityList.add(entity);
        }

        return entityList;
    }

    public static DeviceTemplatePo makeEntity2Po(DeviceTemplateEntity entity) {
        DeviceTemplatePo po = new DeviceTemplatePo();
        po.bind(entity);

        po.setTemplateParam(JsonUtils.buildJsonWithoutException(entity.getTemplateParam()));
        po.setExtendParam(JsonUtils.buildJsonWithoutException(entity.getExtendParam()));
        return po;
    }

    public static DeviceTemplateEntity makePo2Entity(DeviceTemplatePo po) {
        DeviceTemplateEntity entity = new DeviceTemplateEntity();
        entity.bind(po);

        try {
            Map<String, Object> params = JsonUtils.buildObject(po.getTemplateParam(), Map.class);
            if (params != null) {
                entity.setTemplateParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getTemplateParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getTemplateParam());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(po.getExtendParam(), Map.class);
            if (params != null) {
                entity.setExtendParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getExtendParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getExtendParam());
            e.printStackTrace();
        }

        return entity;
    }
}
