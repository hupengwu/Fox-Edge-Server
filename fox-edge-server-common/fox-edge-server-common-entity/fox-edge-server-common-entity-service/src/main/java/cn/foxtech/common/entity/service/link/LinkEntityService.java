package cn.foxtech.common.entity.service.link;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.LinkEntity;
import cn.foxtech.common.entity.entity.LinkPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LinkEntityService extends BaseEntityService {
    @Autowired(required = false)
    private LinkMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    @Override
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    /**
     * 从数据库中获取数据
     *
     * @param linkType 通道类型
     * @return 实体列表
     */
    public List<BaseEntity> selectEntityList(String linkType) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("link_type", linkType);
        List<BaseEntity> existEntity = mapper.selectList(queryWrapper);

        return LinkMaker.makePoList2EntityList(existEntity);
    }

    /**
     * 根据Key特征，查询实体
     */
    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> recordList = super.selectEntityList();

        List<BaseEntity> linkList = new ArrayList<>();
        for (BaseEntity entity : recordList) {
            LinkPo po = (LinkPo) entity;

            LinkEntity link = LinkMaker.makePo2Entity(po);
            linkList.add(link);
        }

        return linkList;
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        LinkPo LinkPo = LinkMaker.makeEntity2Po((LinkEntity) entity);
        super.insertEntity(LinkPo);

        entity.setId(LinkPo.getId());
        entity.setCreateTime(LinkPo.getCreateTime());
        entity.setUpdateTime(LinkPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        LinkPo userPo = LinkMaker.makeEntity2Po((LinkEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        LinkPo LinkPo = LinkMaker.makeEntity2Po((LinkEntity) entity);
        return super.deleteEntity(LinkPo);
    }
}
