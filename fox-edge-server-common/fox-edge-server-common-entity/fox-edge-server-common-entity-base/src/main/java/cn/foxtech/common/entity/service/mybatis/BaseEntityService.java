package cn.foxtech.common.entity.service.mybatis;


import cn.foxtech.common.entity.entity.BaseEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现泛型接口ICacheService的基础抽象类
 */
public abstract class BaseEntityService {
    /**
     * 数据库表的mapper:允许子类直接访问
     */
    protected BaseMapper mapper = null;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public abstract void bindMapper();

    /**
     * 根据id，查询实体
     *
     * @param id 记录的主键id
     */
    public BaseEntity selectEntity(Long id) {
        this.bindMapper();

        return (BaseEntity) mapper.selectById(id);
    }

    /**
     * 根据Key特征，查询实体
     *
     * @param queryWrapper 查询条件
     */
    public BaseEntity selectEntity(QueryWrapper queryWrapper) {
        this.bindMapper();

        return (BaseEntity) mapper.selectOne(queryWrapper);
    }

    /**
     * 根据Key特征，查询实体
     *
     * @param queryWrapper 查询条件
     */
    public List<BaseEntity> selectEntityList(QueryWrapper queryWrapper) {
        this.bindMapper();

        return mapper.selectList(queryWrapper);
    }

    /**
     * 查询全体数据
     */
    public List<BaseEntity> selectEntityList() {
        this.bindMapper();

        List<BaseEntity> entityList = mapper.selectList(null);
        return entityList;
    }

    /**
     * 查询数量
     *
     * @param param
     * @return
     */
    public Long selectEntityListCount(Map<String, Object> param) {
        this.bindMapper();

        return mapper.selectCount(null);
    }

    public List selectListBatchIds(List idList) {
        this.bindMapper();

        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        return mapper.selectBatchIds(idList);
    }

    /**
     * 分組数量
     *
     * @param field 需要分组的字段
     * @return 数量默认输出在id字段
     */
    public List selectListGroupBy(String field) {
        this.bindMapper();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select(field + " ,COUNT(1) AS id").groupBy(field);
        return mapper.selectList(queryWrapper);
    }

    public List selectListGroupBy(String field1, String field2) {
        this.bindMapper();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select(field1 + "," + field2 + " ,COUNT(1) AS id").groupBy(field1, field2);
        return mapper.selectList(queryWrapper);
    }

    /**
     * 查询选项数据
     *
     * @param field 字段名称
     * @return
     */
    public List selectEntityListDistinct(String field) {
        this.bindMapper();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("distinct " + field).orderByAsc(field);
        return mapper.selectList(queryWrapper);
    }

    public List selectEntityListDistinct(String field1, String field2, String value1) {
        this.bindMapper();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("distinct " + field1 + "," + field2).orderByAsc(field2);
        queryWrapper.eq(field1, value1);
        return mapper.selectList(queryWrapper);
    }

    public List selectEntityListDistinct(String field1, String field2, String field3, String value1) {
        this.bindMapper();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("distinct " + field1 + "," + field2 + "," + field3).orderByAsc(field2);
        queryWrapper.eq(field1, value1);
        return mapper.selectList(queryWrapper);
    }

    /**
     * 查询选项数据
     *
     * @param field1 字段名称
     * @param field2 字段名称
     * @return
     */
    public List selectEntityListDistinct(String field1, String field2) {
        this.bindMapper();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("distinct " + field1 + "," + field2).orderByAsc(field1 + "," + field2);
        return mapper.selectList(queryWrapper);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    public void insertEntity(BaseEntity entity) {
        this.bindMapper();

        // 填写时间信息
        Long time = System.currentTimeMillis();
        entity.setCreateTime(time);
        entity.setUpdateTime(time);

        mapper.insert(entity);
    }

    /**
     * 根据Key特征，更新实体
     *
     * @param entity 实体
     */
    public void updateEntity(BaseEntity entity) {
        this.bindMapper();

        // 根据特征找数据
        BaseEntity exist = (BaseEntity) mapper.selectOne((QueryWrapper) entity.makeWrapperKey());
        if (exist == null) {
            return;
        }

        // 将原记录的信息保留，并更新修改时间
        entity.setId(exist.getId());
        entity.setCreateTime(exist.getCreateTime());
        entity.setUpdateTime(System.currentTimeMillis());

        // 刷新数据
        mapper.update(entity, (QueryWrapper) entity.makeWrapperKey());
    }

    /**
     * 根据Key特征，删除实体
     *
     * @param entity 实体
     */
    public int deleteEntity(BaseEntity entity) {
        this.bindMapper();

        return mapper.delete((QueryWrapper) entity.makeWrapperKey());
    }
}
