package cn.foxtech.common.entity.service.extendconfig;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.entity.ExtendConfigPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 * 操作数据库的是PO，但对外呈现的是Entity
 */
@Component
public class ExtendConfigEntityService extends BaseEntityService {
    @Autowired(required = false)
    private ExtendConfigMapper mapper;

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
        return ExtendConfigMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        ExtendConfigPo deviceConfigPo = ExtendConfigMaker.makeEntity2Po((ExtendConfigEntity) entity);
        super.insertEntity(deviceConfigPo);

        entity.setId(deviceConfigPo.getId());
        entity.setCreateTime(deviceConfigPo.getCreateTime());
        entity.setUpdateTime(deviceConfigPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        ExtendConfigPo userPo = ExtendConfigMaker.makeEntity2Po((ExtendConfigEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        ExtendConfigPo deviceConfigPo = ExtendConfigMaker.makeEntity2Po((ExtendConfigEntity) entity);
        return super.deleteEntity(deviceConfigPo);
    }
}
