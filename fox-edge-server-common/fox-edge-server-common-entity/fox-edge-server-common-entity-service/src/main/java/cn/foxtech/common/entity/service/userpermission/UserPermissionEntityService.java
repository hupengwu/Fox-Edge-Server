/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.userpermission;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.UserPermissionEntity;
import cn.foxtech.common.entity.entity.UserPermissionPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.UserPermissionVOFieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserPermissionEntityService extends BaseEntityService {
    @Autowired(required = false)
    private UserPermissionMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return UserPermissionMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        UserPermissionPo userPo = UserPermissionMaker.makeEntity2Po((UserPermissionEntity) entity);
        super.insertEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        UserPermissionPo userPo = UserPermissionMaker.makeEntity2Po((UserPermissionEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        UserPermissionPo userPo = UserPermissionMaker.makeEntity2Po((UserPermissionEntity) entity);
        return super.deleteEntity(userPo);
    }

    public List<UserPermissionPo> selectEntityListByPage(Map<String, Object> param) {
        QueryWrapper queryWrapper = new QueryWrapper<>();

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            BaseVOFieldConstant.makeQueryWrapper(queryWrapper, key, value);

            // 各字段条件
            if (key.equals(UserPermissionVOFieldConstant.field_name)) {
                queryWrapper.eq("name", value);
            }
        }

        List<UserPermissionPo> entitys = mapper.selectList(queryWrapper);

        return entitys;
    }
}
