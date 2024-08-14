/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 配置实体：各服务需要读取预置的全局配置参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_user")
public class UserEntity extends UserBase {
    /**
     * 用户类型：系统用户，是预制用户，不允许删除；非系统用户，是用户创建
     */
    private String userType;

    /**
     * 对象列表
     */
    private String role;

    /**
     * 配置参数
     */
    private String permission;

    /**
     * 对象列表
     */
    private String menu;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = super.makeServiceKeyList();
        return list;
    }


    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.userType);
        list.add(this.role);
        list.add(this.permission);
        list.add(this.menu);

        return list;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.userType = ((String) map.get("userType"));
        this.role = ((String) map.get("role"));
        this.permission = ((String) map.get("permission"));
        this.menu = ((String) map.get("menu"));
    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            UserEntity entity = new UserEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
