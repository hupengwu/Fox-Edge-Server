package cn.foxtech.common.entity.service.userpermission;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.UserPermissionEntity;
import cn.foxtech.common.entity.entity.UserPermissionPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;


public class UserPermissionMaker {
    /**
     * PO转Entity
     *
     * @param deviceList
     * @return
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> deviceList) {
        List<BaseEntity> deviceConfigList = new ArrayList<>();
        for (BaseEntity entity : deviceList) {
            UserPermissionPo po = (UserPermissionPo) entity;


            UserPermissionEntity config = UserPermissionMaker.makePo2Entity(po);
            deviceConfigList.add(config);
        }

        return deviceConfigList;
    }

    public static UserPermissionPo makeEntity2Po(UserPermissionEntity entity) {
        UserPermissionPo result = new UserPermissionPo();
        result.bind(entity);


        result.setPermission(JsonUtils.buildJsonWithoutException(entity.getParams()));
        return result;
    }

    public static UserPermissionEntity makePo2Entity(UserPermissionPo entity) {
        UserPermissionEntity result = new UserPermissionEntity();
        result.bind(entity);


        try {
            List<String> roleList = JsonUtils.buildObject(entity.getPermission(), List.class);
            if (roleList != null) {
                result.getParams().clear();
                result.getParams().addAll(roleList);
            } else {
                System.out.println("触发器配置参数转换Json对象失败：" + entity.getPermission());
            }
        } catch (Exception e) {
            System.out.println("触发器配置参数转换Json对象失败：" + entity.getPermission());
            e.printStackTrace();
        }

        return result;
    }
}
