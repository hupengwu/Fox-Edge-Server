/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.mybatis;


import cn.foxtech.common.entity.entity.BaseEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现泛型接口ICacheService的基础抽象类
 */
public abstract class BaseCacheService {
    /**
     * 数据库表的mapper:允许子类直接访问
     */
    protected BaseMapper mapper = null;

    /**
     * 全局缓存对象容器：key是由派生类的buildEntityKey函数生成的
     */
    private Map<String, BaseEntity> entitys = null;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    public abstract void bindMapper();

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    public void insertEntity(BaseEntity entity) {
        // 填写时间信息
        Long time = System.currentTimeMillis();
        entity.setCreateTime(time);
        entity.setUpdateTime(time);

        mapper.insert(entity);
        this.getEntitys().put(entity.makeServiceKey(), entity);
    }

    /**
     * 根据Key特征，查询实体
     *
     * @param entity 实体
     */
    public BaseEntity selectEntity(BaseEntity entity) {
        return this.getEntity(entity.makeServiceKey());
    }

    /**
     * 根据Key特征，更新实体
     *
     * @param entity 实体
     */
    public void updateEntity(BaseEntity entity) {
        String entityKey = entity.makeServiceKey();

        // 检查：缓存中是否存在该对象
        BaseEntity existEntity = this.getEntitys().get(entityKey);
        if (existEntity == null) {
            return;
        }

        // 检查：值是否发生变化，确定是否真的需要保存到数据库中
        if (existEntity.makeServiceValue().equals(entity.makeServiceValue())) {
            return;
        }

        // 将原记录的信息保留，并更新修改时间
        entity.setId(existEntity.getId());
        entity.setCreateTime(existEntity.getCreateTime());
        entity.setUpdateTime(System.currentTimeMillis());

        // 刷新数据
        mapper.update(entity, (QueryWrapper)entity.makeWrapperKey());

        // 替换缓存对象
        this.getEntitys().put(entityKey, entity);
    }

    /**
     * 根据Key特征，删除实体
     *
     * @param entity 实体
     */
    public void deleteEntity(BaseEntity entity) {
        String entityKey = entity.makeServiceKey();

        // 检查：缓存中是否存在该对象
        BaseEntity existEntity = this.getEntitys().get(entityKey);
        if (existEntity == null) {
            return;
        }

        mapper.deleteById(existEntity);
        this.getEntitys().remove(entityKey);
    }

    /**
     * 实例化单件对象
     */
    public void instance() {
        if (entitys != null) {
            return;
        }

        this.bindMapper();

        // 第一行代码的作用是保证：该函数只有在首次访问时候才会装载全部数据到缓存
        loadEntitys();
    }


    /**
     * 从数据库中装载全局配置参数
     */
    public void loadEntitys() {
        Map<String, BaseEntity> resultEntitys = new HashMap<>();

        // 从数据库中查询配置信息
        List<BaseEntity> entitys = mapper.selectList(null);
        for (BaseEntity entity : entitys) {
            String entityKey = entity.makeServiceKey();
            resultEntitys.put(entityKey, entity);
        }

        this.entitys = resultEntitys;
    }

    /**
     * 获取全局配置
     *
     * @return 实体
     */
    public Map<String, BaseEntity> getEntitys() {
        this.instance();

        return this.entitys;
    }

    /**
     * 获取实体
     *
     * @return 实体
     */
    public BaseEntity getEntity(String entityKey) {
        this.instance();

        if (!this.entitys.containsKey(entityKey)) {
            return null;
        }
        return this.entitys.get(entityKey);
    }

    /**
     * 获取全局实体列表
     *
     * @return 实体列表
     */
    public List<BaseEntity> getEntityList() {
        this.instance();

        List<BaseEntity> entitieList = new ArrayList<>();

        for (Map.Entry<String, BaseEntity> operateEntry : this.entitys.entrySet()) {
            entitieList.add(operateEntry.getValue());
        }

        return entitieList;
    }
}
