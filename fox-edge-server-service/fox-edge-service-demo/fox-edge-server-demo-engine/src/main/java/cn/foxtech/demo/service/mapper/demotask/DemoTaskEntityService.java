/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.demo.service.mapper.demotask;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.demo.service.entity.DemoTaskEntity;
import cn.foxtech.demo.service.entity.DemoTaskPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 * 操作数据库的是PO，但对外呈现的是Entity
 */
@Component
public class DemoTaskEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DemoTaskMapper mapper;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    @Override
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return DemoTaskMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        DemoTaskPo periodTaskPo = DemoTaskMaker.makeEntity2Po((DemoTaskEntity) entity);
        super.insertEntity(periodTaskPo);

        entity.setId(periodTaskPo.getId());
        entity.setCreateTime(periodTaskPo.getCreateTime());
        entity.setUpdateTime(periodTaskPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        DemoTaskPo userPo = DemoTaskMaker.makeEntity2Po((DemoTaskEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        DemoTaskPo periodTaskPo = DemoTaskMaker.makeEntity2Po((DemoTaskEntity) entity);
        return super.deleteEntity(periodTaskPo);
    }
}
