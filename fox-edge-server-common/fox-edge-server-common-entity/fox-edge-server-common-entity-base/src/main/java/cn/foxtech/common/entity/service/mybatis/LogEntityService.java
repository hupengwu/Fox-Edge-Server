/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.mybatis;


import cn.foxtech.common.entity.entity.LogEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 实现泛型接口ICacheService的基础抽象类
 */
public abstract class LogEntityService {
    /**
     * 数据库表的mapper:允许子类直接访问
     */
    protected BaseMapper mapper = null;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public abstract void bindMapper();

    public abstract String getEntityType();

    /**
     * 根据id，查询实体
     *
     * @param id 记录的主键id
     */
    public LogEntity selectEntity(Long id) {
        this.bindMapper();

        return (LogEntity) mapper.selectById(id);
    }

    /**
     * 分页查询数据：从起始ID为pageId开始，最多pageSize条记录
     * 可以根据返回的记录数目是否为pageSize，来判定是否已经最后一页数据
     *
     * @param pageId   起始日志ID
     * @param pageSize 希望获取的日志数量
     * @return 返回的日志记录
     */
    public List<LogEntity> selectEntityListByPage(Long pageId, Long pageSize) {
        this.bindMapper();

        QueryWrapper<LogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("id", pageId);
        queryWrapper.last("limit " + pageSize);

        return mapper.selectList(queryWrapper);
    }

    /**
     * 根据Key特征，查询实体
     *
     * @param queryWrapper 查询条件
     */
    public List<LogEntity> selectEntity(QueryWrapper queryWrapper) {
        this.bindMapper();

        return mapper.selectList(queryWrapper);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    public void insertEntity(LogEntity entity) {
        this.bindMapper();
        mapper.insert(entity);
    }

    /**
     * 根据起始ID和结束ID删除记录
     *
     * @param startId 起始ID
     * @param endId   结束ID
     * @return
     */
    public int deleteEntity(Long startId, Long endId) {
        this.bindMapper();

        QueryWrapper<LogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("id", startId);
        queryWrapper.le("id ", endId);

        return mapper.delete(queryWrapper);
    }
}
