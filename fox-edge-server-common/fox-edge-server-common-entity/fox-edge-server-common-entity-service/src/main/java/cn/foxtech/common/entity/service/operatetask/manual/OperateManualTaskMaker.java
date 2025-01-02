/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.operatetask.manual;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateManualTaskEntity;
import cn.foxtech.common.entity.entity.OperateManualTaskPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperateTemplatePo是数据库格式的对象，OperateTemplateEntity是内存格式的对象，两者需要进行转换
 */
public class OperateManualTaskMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity base : poList) {
            OperateManualTaskPo po = (OperateManualTaskPo) base;

            OperateManualTaskEntity entity = OperateManualTaskMaker.makePo2Entity(po);
            entityList.add(entity);
        }

        return entityList;
    }

    public static OperateManualTaskPo makeEntity2Po(OperateManualTaskEntity entity) {
        OperateManualTaskPo po = new OperateManualTaskPo();
        po.bind(entity);

        po.setTaskParam(JsonUtils.buildJsonWithoutException(entity.getTaskParam()));
        return po;
    }

    public static OperateManualTaskEntity makePo2Entity(OperateManualTaskPo po) {
        OperateManualTaskEntity entity = new OperateManualTaskEntity();
        entity.bind(po);

        try {
            List<Map<String, Object>> params = JsonUtils.buildObject(po.getTaskParam(), List.class);
            if (params != null) {
                entity.setTaskParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + po.getTaskName() + ":" + po.getTaskParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + po.getTaskName() + ":" + po.getTaskParam());
            e.printStackTrace();
        }

        return entity;
    }
}
