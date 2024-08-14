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
        List<BaseEntity> operateRecordList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            OperateManualTaskPo po = (OperateManualTaskPo) entity;

            OperateManualTaskEntity config = OperateManualTaskMaker.makePo2Entity(po);
            operateRecordList.add(config);
        }

        return operateRecordList;
    }

    public static OperateManualTaskPo makeEntity2Po(OperateManualTaskEntity entity) {
        OperateManualTaskPo result = new OperateManualTaskPo();
        result.bind(entity);

        result.setTaskParam(JsonUtils.buildJsonWithoutException(entity.getTaskParam()));
        return result;
    }

    public static OperateManualTaskEntity makePo2Entity(OperateManualTaskPo entity) {
        OperateManualTaskEntity result = new OperateManualTaskEntity();
        result.bind(entity);

        try {
            List<Map<String, Object>> params = JsonUtils.buildObject(entity.getTaskParam(), List.class);
            if (params != null) {
                result.setTaskParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskParam() + ":" + entity.getTaskParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskParam() + ":" + entity.getTaskParam());
            e.printStackTrace();
        }

        return result;
    }
}
