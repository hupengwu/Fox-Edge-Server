package cn.foxtech.common.entity.service.devicerecord;


import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceRecordVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceRecordEntity;
import cn.foxtech.common.entity.entity.DeviceRecordPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DeviceRecordEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DeviceRecordEntityMapper mapper;


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

        List<BaseEntity> deviceRecordList = new ArrayList<>();
        for (BaseEntity entity : recordList) {
            DeviceRecordPo po = (DeviceRecordPo) entity;

            DeviceRecordEntity config = DeviceRecordMaker.makePo2Entity(po);
            deviceRecordList.add(config);
        }

        return deviceRecordList;
    }


    @Override
    public void insertEntity(BaseEntity entity) {
        DeviceRecordPo po = DeviceRecordMaker.makeEntity2Po((DeviceRecordEntity) entity);
        super.insertEntity(po);
    }
}
