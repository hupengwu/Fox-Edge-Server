/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicevaluerecord;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceValueRecordEntity;
import cn.foxtech.common.entity.entity.DeviceValueRecordPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeviceValueRecordEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DeviceValueRecordEntityMapper mapper;


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

        List<BaseEntity> deviceValueRecordList = new ArrayList<>();
        for (BaseEntity entity : recordList) {
            DeviceValueRecordPo po = (DeviceValueRecordPo) entity;

            DeviceValueRecordEntity config = DeviceValueRecordMaker.makePo2Entity(po);
            deviceValueRecordList.add(config);
        }

        return deviceValueRecordList;
    }


    @Override
    public void insertEntity(BaseEntity entity) {
        DeviceValueRecordPo po = DeviceValueRecordMaker.makeEntity2Po((DeviceValueRecordEntity) entity);
        super.insertEntity(po);

        entity.setId(po.getId());
        entity.setCreateTime(po.getCreateTime());
        entity.setUpdateTime(po.getUpdateTime());
    }

    /**
     * 删除旧数据，只保留少数的最新的部分数据
     *
     * @param retainCount 需要保留的数据数量
     */
    public void delete(int retainCount) {
        Integer sumCount = mapper.executeSelectCount("SELECT COUNT(1) FROM  tb_device_value_record");
        if (sumCount <= retainCount) {
            return;
        }

        // 删除旧记录
        String sql = String.format("DELETE FROM  tb_device_value_record order BY id LIMIT  %d", sumCount - retainCount);
        mapper.executeDelete(sql);
    }
}
