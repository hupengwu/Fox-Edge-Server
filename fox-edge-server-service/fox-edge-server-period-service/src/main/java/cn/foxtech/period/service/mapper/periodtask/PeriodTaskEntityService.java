package cn.foxtech.period.service.mapper.periodtask;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.period.service.entity.PeriodTaskEntity;
import cn.foxtech.period.service.entity.PeriodTaskPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 * 操作数据库的是PO，但对外呈现的是Entity
 */
@Component
public class PeriodTaskEntityService extends BaseEntityService {
    @Autowired(required = false)
    private PeriodTaskMapper mapper;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    @Override
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return PeriodTaskMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        PeriodTaskPo periodTaskPo = PeriodTaskMaker.makeEntity2Po((PeriodTaskEntity) entity);
        super.insertEntity(periodTaskPo);

        entity.setId(periodTaskPo.getId());
        entity.setCreateTime(periodTaskPo.getCreateTime());
        entity.setUpdateTime(periodTaskPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        PeriodTaskPo userPo = PeriodTaskMaker.makeEntity2Po((PeriodTaskEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        PeriodTaskPo periodTaskPo = PeriodTaskMaker.makeEntity2Po((PeriodTaskEntity) entity);
        return super.deleteEntity(periodTaskPo);
    }
}
