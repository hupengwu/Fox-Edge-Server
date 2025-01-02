/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.operatetask.monitor;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskPo;
import cn.foxtech.common.utils.json.JsonUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperateTemplatePo是数据库格式的对象，OperateTemplateEntity是内存格式的对象，两者需要进行转换
 */
public class OperateMonitorTaskMaker {
    private static final Logger logger = Logger.getLogger(OperateMonitorTaskMaker.class);

    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity base : poList) {
            OperateMonitorTaskPo po = (OperateMonitorTaskPo) base;

            OperateMonitorTaskEntity entity = OperateMonitorTaskMaker.makePo2Entity(po);
            entityList.add(entity);
        }

        return entityList;
    }

    public static OperateMonitorTaskPo makeEntity2Po(OperateMonitorTaskEntity entity) {
        OperateMonitorTaskPo po = new OperateMonitorTaskPo();
        po.bind(entity);

        po.setDeviceIds(JsonUtils.buildJsonWithoutException(entity.getDeviceIds()));
        po.setTemplateParam(JsonUtils.buildJsonWithoutException(entity.getTemplateParam()));
        po.setTaskParam(JsonUtils.buildJsonWithoutException(entity.getTaskParam()));

        return po;
    }

    public static OperateMonitorTaskEntity makePo2Entity(OperateMonitorTaskPo po) {
        OperateMonitorTaskEntity entity = new OperateMonitorTaskEntity();
        entity.bind(po);

        try {
            List<Map<String, Object>> params = JsonUtils.buildObject(po.getTemplateParam(), List.class);
            if (params != null) {
                entity.setTemplateParam(params);
            } else {
                logger.error("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getTemplateParam());
            }
        } catch (Exception e) {
            logger.error("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getTemplateParam());
        }

        try {
            List<Long> deviceIds = JsonUtils.buildObject(po.getDeviceIds(), List.class);
            if (deviceIds != null) {
                entity.getDeviceIds().addAll(deviceIds);
            } else {
                logger.error("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getDeviceIds());
            }
        } catch (Exception e) {
            logger.error("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getDeviceIds());
        }

        try {
            Map<String, Object> taskParam = JsonUtils.buildObject(po.getTaskParam(), Map.class);
            if (taskParam != null) {
                entity.getTaskParam().putAll(taskParam);
            } else {
                logger.error("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getTaskParam());
            }
        } catch (Exception e) {
            logger.error("设备配置参数转换Json对象失败：" + po.getTemplateName() + ":" + po.getTaskParam());
        }

        // 补充缺省值
        entity.setDefaultValue();

        return entity;
    }
}
