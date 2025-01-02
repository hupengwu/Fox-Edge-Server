/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.operatetask.channel;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateChannelTaskEntity;
import cn.foxtech.common.entity.entity.OperateChannelTaskPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperateTemplatePo是数据库格式的对象，OperateTemplateEntity是内存格式的对象，两者需要进行转换
 */
public class OperateChannelTaskMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity base : poList) {
            OperateChannelTaskPo po = (OperateChannelTaskPo) base;

            OperateChannelTaskEntity entity = OperateChannelTaskMaker.makePo2Entity(po);
            entityList.add(entity);
        }

        return entityList;
    }

    public static OperateChannelTaskPo makeEntity2Po(OperateChannelTaskEntity entity) {
        OperateChannelTaskPo po = new OperateChannelTaskPo();
        po.bind(entity);

        po.setTaskParam(JsonUtils.buildJsonWithoutException(entity.getTaskParam()));
        return po;
    }

    public static OperateChannelTaskEntity makePo2Entity(OperateChannelTaskPo po) {
        OperateChannelTaskEntity entity = new OperateChannelTaskEntity();
        entity.bind(po);

        try {
            Map<String, Object> params = JsonUtils.buildObject(po.getTaskParam(), Map.class);
            if (params != null) {
                entity.setTaskParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + po.getTaskParam() + ":" + po.getTaskParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + po.getTaskParam() + ":" + po.getTaskParam());
            e.printStackTrace();
        }

        return entity;
    }
}
