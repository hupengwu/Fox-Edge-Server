/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.operaterecord;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateRecordPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateRecordVOFieldConstant;
import cn.foxtech.common.entity.entity.OperateRecordEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OperateRecordEntityService extends BaseEntityService {
    @Autowired(required = false)
    private OperateRecordEntityMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    /**
     * 根据Key特征，查询实体
     *
     * @param queryWrapper 查询条件
     */
    @Override
    public List<BaseEntity> selectEntityList(QueryWrapper queryWrapper) {
        List<BaseEntity> recordList = super.selectEntityList(queryWrapper);

        List<BaseEntity> operateRecordList = new ArrayList<>();
        for (BaseEntity entity : recordList) {
            OperateRecordPo po = (OperateRecordPo) entity;

            OperateRecordEntity config = OperateRecordMaker.makePo2Entity(po);
            operateRecordList.add(config);
        }

        return operateRecordList;
    }


    @Override
    public void insertEntity(BaseEntity entity) {
        OperateRecordPo po = OperateRecordMaker.makeEntity2Po((OperateRecordEntity) entity);
        super.insertEntity(po);
    }

    /**
     * 删除旧数据，只保留少数的最新的部分数据
     *
     * @param retainCount 需要保留的数据数量
     */
    public void delete(int retainCount) {
        // 删除旧记录
        String sql = String.format("DELETE FROM tb_operate_record WHERE id NOT IN (SELECT t1.id AS id FROM (SELECT t.id FROM tb_operate_record t ORDER BY t.id LIMIT %d) t1)", retainCount);
        mapper.executeDelete(sql);
    }
}
