/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.repocomp;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.entity.RepoCompPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Getter(value = AccessLevel.PUBLIC)
public class RepoCompEntityService extends BaseEntityService {
    @Autowired(required = false)
    private RepoCompMapper mapper;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public BaseEntity selectEntity(Long id) {
        RepoCompPo po = (RepoCompPo) super.selectEntity(id);
        if (po == null) {
            return null;
        }

        return RepoCompEntityMaker.makePo2Entity(po);
    }

    @Override
    public List<BaseEntity> selectListBatchIds(List idList) {
        List<BaseEntity> poList = super.selectListBatchIds(idList);

        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            RepoCompPo po = (RepoCompPo) entity;

            RepoCompEntity RepoComp = RepoCompEntityMaker.makePo2Entity(po);
            entityList.add(RepoComp);
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
            RepoCompPo po = (RepoCompPo) entity;

            RepoCompEntity RepoComp = RepoCompEntityMaker.makePo2Entity(po);
            entityList.add(RepoComp);
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
        RepoCompPo RepoCompPo = RepoCompEntityMaker.makeEntity2Po((RepoCompEntity) entity);
        super.insertEntity(RepoCompPo);

        entity.setId(RepoCompPo.getId());
        entity.setCreateTime(RepoCompPo.getCreateTime());
        entity.setUpdateTime(RepoCompPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        RepoCompPo RepoCompPo = RepoCompEntityMaker.makeEntity2Po((RepoCompEntity) entity);
        super.updateEntity(RepoCompPo);

        entity.setId(RepoCompPo.getId());
        entity.setCreateTime(RepoCompPo.getCreateTime());
        entity.setUpdateTime(RepoCompPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        RepoCompPo RepoCompPo = RepoCompEntityMaker.makeEntity2Po((RepoCompEntity) entity);
        return super.deleteEntity(RepoCompPo);
    }
}
