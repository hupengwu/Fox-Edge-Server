package cn.foxtech.common.entity.entity;

import cn.foxtech.common.utils.number.NumberUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ChannelStatusEntity extends BaseEntity {
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 通道类型
     */
    private String channelType;


    /**
     * 是否打开
     */
    private boolean open = false;

    /**
     * 状态参数
     */
    private Map<String, Object> channelParam = new HashMap<>();

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.channelType);
        list.add(this.channelName);


        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_name", this.channelName);
        queryWrapper.eq("channel_type", this.channelType);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.open);
        list.add(this.channelParam);
        return list;
    }

    public void bind(ChannelEntity channelEntity) {
        this.setId(channelEntity.getId());
        this.setCreateTime(channelEntity.getCreateTime());
        this.setUpdateTime(channelEntity.getUpdateTime());

        this.channelType = channelEntity.getChannelType();
        this.channelName = channelEntity.getChannelName();
    }

    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            ChannelStatusEntity entity = new ChannelStatusEntity();
            entity.setId(NumberUtils.makeLong(map.get("id")));
            entity.setCreateTime(NumberUtils.makeLong(map.get("createTime")));
            entity.setUpdateTime(NumberUtils.makeLong(map.get("updateTime")));


            entity.setChannelName((String) map.get("channelName"));
            entity.setChannelType((String) map.get("channelType"));
            entity.setOpen((Boolean) map.getOrDefault("open", false));
            entity.setChannelParam((Map<String, Object>) map.getOrDefault("channelParam", new HashMap<>()));

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
