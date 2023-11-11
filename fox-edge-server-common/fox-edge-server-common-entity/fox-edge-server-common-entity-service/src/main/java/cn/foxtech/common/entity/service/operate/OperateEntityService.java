package cn.foxtech.common.entity.service.operate;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.OperatePo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OperateEntityService extends BaseEntityService {
    @Autowired(required = false)
    private OperateMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    /**
     * 根据Key特征，查询实体
     */
    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();

        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            OperatePo po = (OperatePo) entity;

            OperateEntity Operate = OperateEntityMaker.makePo2Entity(po);
            entityList.add(Operate);
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
        OperatePo OperatePo = OperateEntityMaker.makeEntity2Po((OperateEntity) entity);
        super.insertEntity(OperatePo);

        entity.setId(OperatePo.getId());
        entity.setCreateTime(OperatePo.getCreateTime());
        entity.setUpdateTime(OperatePo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        OperatePo OperatePo = OperateEntityMaker.makeEntity2Po((OperateEntity) entity);
        super.updateEntity(OperatePo);

        entity.setId(OperatePo.getId());
        entity.setCreateTime(OperatePo.getCreateTime());
        entity.setUpdateTime(OperatePo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        OperatePo OperatePo = OperateEntityMaker.makeEntity2Po((OperateEntity) entity);
        return super.deleteEntity(OperatePo);
    }
}
