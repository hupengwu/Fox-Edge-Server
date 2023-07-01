package cn.foxtech.common.entity.service.operatetask.monitor;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperateMonitorTaskEntityService extends BaseEntityService {
    @Autowired(required = false)
    private OperateMonitorTaskEntityMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }


    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return OperateMonitorTaskMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        OperateMonitorTaskPo deviceConfigPo = OperateMonitorTaskMaker.makeEntity2Po((OperateMonitorTaskEntity) entity);
        super.insertEntity(deviceConfigPo);

        entity.setId(deviceConfigPo.getId());
        entity.setCreateTime(deviceConfigPo.getCreateTime());
        entity.setUpdateTime(deviceConfigPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        OperateMonitorTaskPo userPo = OperateMonitorTaskMaker.makeEntity2Po((OperateMonitorTaskEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        OperateMonitorTaskPo deviceConfigPo = OperateMonitorTaskMaker.makeEntity2Po((OperateMonitorTaskEntity) entity);
        return super.deleteEntity(deviceConfigPo);
    }
}
