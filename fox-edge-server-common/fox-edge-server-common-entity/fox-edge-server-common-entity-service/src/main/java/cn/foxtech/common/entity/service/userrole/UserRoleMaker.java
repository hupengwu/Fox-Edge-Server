/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.userrole;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.UserRoleEntity;
import cn.foxtech.common.entity.entity.UserRolePo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;


public class UserRoleMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> resultList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            UserRolePo po = (UserRolePo) entity;


            UserRoleEntity result = UserRoleMaker.makePo2Entity(po);
            resultList.add(result);
        }

        return resultList;
    }

    public static UserRolePo makeEntity2Po(UserRoleEntity entity) {
        UserRolePo result = new UserRolePo();
        result.bind(entity);


        result.setRole(JsonUtils.buildJsonWithoutException(entity.getParams()));
        return result;
    }

    public static UserRoleEntity makePo2Entity(UserRolePo entity) {
        UserRoleEntity result = new UserRoleEntity();
        result.bind(entity);


        try {
            List<String> objList = JsonUtils.buildObject(entity.getRole(), List.class);
            if (objList != null) {
                result.getParams().clear();
                result.getParams().addAll(objList);
            } else {
                System.out.println("触发器配置参数转换Json对象失败：" + entity.getRole());
            }
        } catch (Exception e) {
            System.out.println("触发器配置参数转换Json对象失败：" + entity.getRole());
            e.printStackTrace();
        }

        return result;
    }
}
