/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.repocomp;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.entity.RepoCompPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RepoCompPo是数据库格式的对象，RepoCompEntity是内存格式的对象，两者需要进行转换
 */
public class RepoCompEntityMaker {
    /**
     * PO转Entity
     *
     * @param poList po列表
     * @return 对象列表
     */
    public static List<BaseEntity> makePoList2EntityList(List poList) {
        List<BaseEntity> resultList = new ArrayList<>();
        for (Object entity : poList) {
            RepoCompPo po = (RepoCompPo) entity;


            RepoCompEntity result = RepoCompEntityMaker.makePo2Entity(po);
            resultList.add(result);
        }

        return resultList;
    }

    public static RepoCompPo makeEntity2Po(RepoCompEntity entity) {
        RepoCompPo result = new RepoCompPo();
        result.bind(entity);

        result.setCompParam(JsonUtils.buildJsonWithoutException(entity.getCompParam()));
        return result;
    }

    public static RepoCompEntity makePo2Entity(RepoCompPo entity) {
        RepoCompEntity result = new RepoCompEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getCompParam(), Map.class);
            if (params != null) {
                result.setCompParam(params);
            } else {
                System.out.println("配置参数转换Json对象失败：" + entity.getCompName() + ":" + entity.getCompParam());
            }
        } catch (Exception e) {
            System.out.println("配置参数转换Json对象失败：" + entity.getCompName() + ":" + entity.getCompParam());
            e.printStackTrace();
        }


        return result;
    }
}
