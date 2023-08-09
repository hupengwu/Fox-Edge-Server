package cn.foxtech.common.entity.service.channel;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.ChannelPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChannelEntityService extends BaseEntityService {
    @Autowired(required = false)
    private ChannelMapper mapper;


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
     * @param channelType 通道类型
     * @return 实体列表
     */
    public List<BaseEntity> selectEntityList(String channelType) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("channel_type", channelType);
        List<BaseEntity> existEntity = mapper.selectList(queryWrapper);

        return ChannelMaker.makePoList2EntityList(existEntity);
    }

    /**
     * 根据Key特征，查询实体
     */
    @Override
    public List<BaseEntity> selectEntityList() {
        List<BaseEntity> recordList = super.selectEntityList();

        List<BaseEntity> channelList = new ArrayList<>();
        for (BaseEntity entity : recordList) {
            ChannelPo po = (ChannelPo) entity;

            ChannelEntity channel = ChannelMaker.makePo2Entity(po);
            channelList.add(channel);
        }

        return channelList;
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        ChannelPo ChannelPo = ChannelMaker.makeEntity2Po((ChannelEntity) entity);
        super.insertEntity(ChannelPo);

        entity.setId(ChannelPo.getId());
        entity.setCreateTime(ChannelPo.getCreateTime());
        entity.setUpdateTime(ChannelPo.getUpdateTime());
    }

    @Override
    public void updateEntity(BaseEntity entity) {
        ChannelPo userPo = ChannelMaker.makeEntity2Po((ChannelEntity) entity);
        super.updateEntity(userPo);

        entity.setId(userPo.getId());
        entity.setCreateTime(userPo.getCreateTime());
        entity.setUpdateTime(userPo.getUpdateTime());
    }

    @Override
    public int deleteEntity(BaseEntity entity) {
        ChannelPo ChannelPo = ChannelMaker.makeEntity2Po((ChannelEntity) entity);
        return super.deleteEntity(ChannelPo);
    }
}
