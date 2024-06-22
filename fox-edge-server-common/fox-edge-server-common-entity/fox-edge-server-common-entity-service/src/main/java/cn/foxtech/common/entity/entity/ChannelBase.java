package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ChannelBase extends BaseEntity {
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 通道类型
     */
    private String channelType;

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
        return list;
    }

    public void bind(ChannelBase other) {
        super.bind(other);

        this.channelType = other.channelType;
        this.channelName = other.channelName;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.channelType = (String) map.get("channelType");
        this.channelName = (String) map.get("channelName");
    }
}
