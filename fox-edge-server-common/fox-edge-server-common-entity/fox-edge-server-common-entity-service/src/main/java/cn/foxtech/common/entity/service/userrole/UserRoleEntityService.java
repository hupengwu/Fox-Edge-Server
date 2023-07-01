package cn.foxtech.common.entity.service.userrole;


import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.UserRoleEntity;
import cn.foxtech.common.entity.entity.UserRolePo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.foxtech.common.entity.constant.UserRoleVOFieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserRoleEntityService extends BaseEntityService {
    @Autowired(required = false)
    private UserRoleMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return UserRoleMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        UserRolePo userPo = UserRoleMaker.makeEntity2Po((UserRoleEntity) entity);
        super.insertEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        UserRolePo userPo = UserRoleMaker.makeEntity2Po((UserRoleEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        UserRolePo userPo = UserRoleMaker.makeEntity2Po((UserRoleEntity) entity);
        return super.deleteEntity(userPo);
    }

    public List<UserRolePo> selectEntityListByPage(Map<String, Object> param) {
        QueryWrapper queryWrapper = new QueryWrapper<>();

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            BaseVOFieldConstant.makeQueryWrapper(queryWrapper, key, value);

            // 各字段条件
            if (key.equals(UserRoleVOFieldConstant.field_name)) {
                queryWrapper.eq("name", value);
            }
        }

        List<UserRolePo> entitys = mapper.selectList(queryWrapper);

        return entitys;
    }
}
