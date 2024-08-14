/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.extendconfig;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.entity.ExtendConfigPo;
import cn.foxtech.common.entity.entity.ExtendParam;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 */
public class ExtendConfigMaker {
    /**
     * PO转Entity
     *
     * @param entityList 实体列表
     * @return PO列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> entityList) {
        List<BaseEntity> entityConfigList = new ArrayList<>();
        for (BaseEntity entity : entityList) {
            ExtendConfigPo po = (ExtendConfigPo) entity;


            ExtendConfigEntity config = ExtendConfigMaker.makePo2Entity(po);
            entityConfigList.add(config);
        }

        return entityConfigList;
    }

    public static ExtendConfigPo makeEntity2Po(ExtendConfigEntity entity) {
        ExtendConfigPo result = new ExtendConfigPo();
        result.bind(entity);

        result.setExtendParam(JsonUtils.buildJsonWithoutException(entity.getExtendParam()));
        return result;
    }

    public static ExtendConfigEntity makePo2Entity(ExtendConfigPo entity) {
        ExtendConfigEntity result = new ExtendConfigEntity();
        result.bind(entity);

        try {
            ExtendParam params = JsonUtils.buildObject(entity.getExtendParam(), ExtendParam.class);
            if (params != null) {
                result.setExtendParam(params);
            } else {
                System.out.println("参数转换Json对象失败：" + entity.getExtendName() + ":" + entity.getExtendParam());
            }
        } catch (Exception e) {
            System.out.println("参数转换Json对象失败：" + entity.getExtendName() + ":" + entity.getExtendParam());
            e.printStackTrace();
        }

        return result;
    }
}
