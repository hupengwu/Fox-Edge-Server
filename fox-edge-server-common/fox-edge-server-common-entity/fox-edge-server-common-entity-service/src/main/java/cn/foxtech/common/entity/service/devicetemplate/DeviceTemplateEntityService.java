/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicetemplate;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceTemplateEntity;
import cn.foxtech.common.entity.entity.DeviceTemplatePo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceTemplateEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DeviceTemplateEntityMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }


    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return DeviceTemplateMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        DeviceTemplatePo po = DeviceTemplateMaker.makeEntity2Po((DeviceTemplateEntity) entity);
        super.insertEntity(po);

        entity.setId(po.getId());
        entity.setCreateTime(po.getCreateTime());
        entity.setUpdateTime(po.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        DeviceTemplatePo po = DeviceTemplateMaker.makeEntity2Po((DeviceTemplateEntity) entity);
        super.updateEntity(po);

        entity.setId(po.getId());
        entity.setCreateTime(po.getCreateTime());
        entity.setUpdateTime(po.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        DeviceTemplatePo po = DeviceTemplateMaker.makeEntity2Po((DeviceTemplateEntity) entity);
        return super.deleteEntity(po);
    }
}
