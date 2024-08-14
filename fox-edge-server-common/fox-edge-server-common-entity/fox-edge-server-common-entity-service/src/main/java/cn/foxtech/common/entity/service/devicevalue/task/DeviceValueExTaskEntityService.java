/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicevalue.task;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceValueExTaskEntity;
import cn.foxtech.common.entity.entity.DeviceValueExTaskPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceValueExTaskEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DeviceValueExTaskEntityMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }


    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return DeviceValueExTaskMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        DeviceValueExTaskPo deviceConfigPo = DeviceValueExTaskMaker.makeEntity2Po((DeviceValueExTaskEntity) entity);
        super.insertEntity(deviceConfigPo);

        entity.setId(deviceConfigPo.getId());
        entity.setCreateTime(deviceConfigPo.getCreateTime());
        entity.setUpdateTime(deviceConfigPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        DeviceValueExTaskPo userPo = DeviceValueExTaskMaker.makeEntity2Po((DeviceValueExTaskEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        DeviceValueExTaskPo deviceConfigPo = DeviceValueExTaskMaker.makeEntity2Po((DeviceValueExTaskEntity) entity);
        return super.deleteEntity(deviceConfigPo);
    }
}
