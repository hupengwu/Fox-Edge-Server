/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.operatetask.channel;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateChannelTaskEntity;
import cn.foxtech.common.entity.entity.OperateChannelTaskPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperateChannelTaskEntityService extends BaseEntityService {
    @Autowired(required = false)
    private OperateChannelTaskEntityMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }


    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return OperateChannelTaskMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        OperateChannelTaskPo deviceConfigPo = OperateChannelTaskMaker.makeEntity2Po((OperateChannelTaskEntity) entity);
        super.insertEntity(deviceConfigPo);

        entity.setId(deviceConfigPo.getId());
        entity.setCreateTime(deviceConfigPo.getCreateTime());
        entity.setUpdateTime(deviceConfigPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        OperateChannelTaskPo userPo = OperateChannelTaskMaker.makeEntity2Po((OperateChannelTaskEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        OperateChannelTaskPo deviceConfigPo = OperateChannelTaskMaker.makeEntity2Po((OperateChannelTaskEntity) entity);
        return super.deleteEntity(deviceConfigPo);
    }
}
