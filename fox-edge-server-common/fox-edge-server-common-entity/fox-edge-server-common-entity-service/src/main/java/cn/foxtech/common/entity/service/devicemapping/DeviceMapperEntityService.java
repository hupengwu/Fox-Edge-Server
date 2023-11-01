package cn.foxtech.common.entity.service.devicemapping;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceMapperEntity;
import cn.foxtech.common.entity.entity.DeviceMapperPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter(value = AccessLevel.PUBLIC)
public class DeviceMapperEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DeviceMapperEntityMapper mapper;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public BaseEntity selectEntity(Long id) {
        DeviceMapperPo po = (DeviceMapperPo) super.selectEntity(id);
        if (po == null) {
            return null;
        }

        return DeviceMapperEntityMaker.makePo2Entity(po);
    }

    @Override
    public List<BaseEntity> selectListBatchIds(List idList) {
        List<BaseEntity> poList = super.selectListBatchIds(idList);

        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            DeviceMapperPo po = (DeviceMapperPo) entity;

            DeviceMapperEntity Device = DeviceMapperEntityMaker.makePo2Entity(po);
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
            DeviceMapperPo po = (DeviceMapperPo) entity;

            DeviceMapperEntity Device = DeviceMapperEntityMaker.makePo2Entity(po);
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
        DeviceMapperPo DeviceMapperPo = DeviceMapperEntityMaker.makeEntity2Po((DeviceMapperEntity) entity);
        super.insertEntity(DeviceMapperPo);

        entity.setId(DeviceMapperPo.getId());
        entity.setCreateTime(DeviceMapperPo.getCreateTime());
        entity.setUpdateTime(DeviceMapperPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        DeviceMapperPo DeviceMapperPo = DeviceMapperEntityMaker.makeEntity2Po((DeviceMapperEntity) entity);
        super.updateEntity(DeviceMapperPo);

        entity.setId(DeviceMapperPo.getId());
        entity.setCreateTime(DeviceMapperPo.getCreateTime());
        entity.setUpdateTime(DeviceMapperPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        DeviceMapperPo DeviceMapperPo = DeviceMapperEntityMaker.makeEntity2Po((DeviceMapperEntity) entity);
        return super.deleteEntity(DeviceMapperPo);
    }
}
