package cn.foxtech.common.entity.service.probe;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ProbeEntity;
import cn.foxtech.common.entity.entity.ProbePo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.ProbeVOFieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 * 操作数据库的是PO，但对外呈现的是Entity
 */
@Component
public class ProbeEntityService extends BaseEntityService {
    @Autowired(required = false)
    private ProbeEntityMapper mapper;

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
        return ProbeEntityMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        ProbePo probePo = ProbeEntityMaker.makeEntity2Po((ProbeEntity) entity);
        super.insertEntity(probePo);

        entity.setId(probePo.getId());
        entity.setCreateTime(probePo.getCreateTime());
        entity.setUpdateTime(probePo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        ProbePo userPo = ProbeEntityMaker.makeEntity2Po((ProbeEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        ProbePo probePo = ProbeEntityMaker.makeEntity2Po((ProbeEntity) entity);
        return super.deleteEntity(probePo);
    }
}
