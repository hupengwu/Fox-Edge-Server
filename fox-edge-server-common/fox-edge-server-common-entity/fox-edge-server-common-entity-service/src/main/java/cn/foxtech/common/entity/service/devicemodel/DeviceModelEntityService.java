package cn.foxtech.common.entity.service.devicemodel;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.entity.DeviceModelPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Getter(value = AccessLevel.PUBLIC)
public class DeviceModelEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DeviceModelEntityMapper mapper;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public BaseEntity selectEntity(Long id) {
        DeviceModelPo po = (DeviceModelPo) super.selectEntity(id);
        if (po == null) {
            return null;
        }

        return DeviceModelEntityMaker.makePo2Entity(po);
    }

    @Override
    public List<BaseEntity> selectListBatchIds(List idList) {
        List<BaseEntity> poList = super.selectListBatchIds(idList);

        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            DeviceModelPo po = (DeviceModelPo) entity;

            DeviceModelEntity Device = DeviceModelEntityMaker.makePo2Entity(po);
            entityList.add(Device);
        }

        return entityList;
    }


    /**
     * 根据Key特征，查询实体
     */
    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();

        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            DeviceModelPo po = (DeviceModelPo) entity;

            DeviceModelEntity Device = DeviceModelEntityMaker.makePo2Entity(po);
            entityList.add(Device);
        }

        return entityList;
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        DeviceModelPo DeviceModelPo = DeviceModelEntityMaker.makeEntity2Po((DeviceModelEntity) entity);
        super.insertEntity(DeviceModelPo);

        entity.setId(DeviceModelPo.getId());
        entity.setCreateTime(DeviceModelPo.getCreateTime());
        entity.setUpdateTime(DeviceModelPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        DeviceModelPo DeviceModelPo = DeviceModelEntityMaker.makeEntity2Po((DeviceModelEntity) entity);
        super.updateEntity(DeviceModelPo);

        entity.setId(DeviceModelPo.getId());
        entity.setCreateTime(DeviceModelPo.getCreateTime());
        entity.setUpdateTime(DeviceModelPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        DeviceModelPo DeviceModelPo = DeviceModelEntityMaker.makeEntity2Po((DeviceModelEntity) entity);
        return super.deleteEntity(DeviceModelPo);
    }
}
