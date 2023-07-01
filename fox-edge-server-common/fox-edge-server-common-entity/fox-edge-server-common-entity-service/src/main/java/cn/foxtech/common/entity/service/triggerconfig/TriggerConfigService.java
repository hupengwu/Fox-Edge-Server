package cn.foxtech.common.entity.service.triggerconfig;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.TriggerConfigEntity;
import cn.foxtech.common.entity.entity.TriggerConfigPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.TriggerConfigVOFieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * TriggerConfigPo是数据库格式的对象，TriggerConfigEntity是内存格式的对象，两者需要进行转换
 * 操作数据库的是PO，但对外呈现的是Entity
 */
@Component
public class TriggerConfigService extends BaseEntityService {
    @Autowired(required = false)
    private TriggerConfigMapper mapper;

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
        return TriggerConfigMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        TriggerConfigPo triggerConfigPo = TriggerConfigMaker.makeEntity2Po((TriggerConfigEntity) entity);
        super.insertEntity(triggerConfigPo);

        entity.setId(triggerConfigPo.getId());
        entity.setCreateTime(triggerConfigPo.getCreateTime());
        entity.setUpdateTime(triggerConfigPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        TriggerConfigPo userPo = TriggerConfigMaker.makeEntity2Po((TriggerConfigEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        TriggerConfigPo triggerConfigPo = TriggerConfigMaker.makeEntity2Po((TriggerConfigEntity) entity);
        return super.deleteEntity(triggerConfigPo);
    }
}
