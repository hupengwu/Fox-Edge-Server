package cn.foxtech.common.entity.service.usermenu;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.UserMenuEntity;
import cn.foxtech.common.entity.entity.UserMenuPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.UserMenuVOFieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserMenuEntityService extends BaseEntityService {
    @Autowired(required = false)
    private UserMenuMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> poList = super.selectEntityList();
        return UserMenuMaker.makePoList2EntityList(poList);
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        UserMenuPo userPo = UserMenuMaker.makeEntity2Po((UserMenuEntity) entity);
        super.insertEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        UserMenuPo userPo = UserMenuMaker.makeEntity2Po((UserMenuEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        UserMenuPo userPo = UserMenuMaker.makeEntity2Po((UserMenuEntity) entity);
        return super.deleteEntity(userPo);
    }

    public List<UserMenuPo> selectEntityListByPage(Map<String, Object> param) {
        QueryWrapper queryWrapper = new QueryWrapper<>();

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            BaseVOFieldConstant.makeQueryWrapper(queryWrapper, key, value);

            // 各字段条件
            if (key.equals(UserMenuVOFieldConstant.field_name)) {
                queryWrapper.eq("name", value);
            }
        }

        List<UserMenuPo> entitys = mapper.selectList(queryWrapper);

        return entitys;
    }
}
