package cn.foxtech.common.entity.entity;


import cn.foxtech.common.utils.number.NumberUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceStatusEntity extends BaseEntity {
    /**
     * 最近通信成功的时间，方便判定设备是否断连
     */
    private long commSuccessTime = 0;

    /**
     * 最近访问失败的时间
     */
    private long commFailedTime = 0;

    /**
     * 连续访问失败的次数
     */
    private int commFailedCount = 0;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.getId());

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", this.getId());

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.commSuccessTime);
        list.add(this.commFailedTime);
        list.add(this.commFailedCount);
        return list;
    }

    public void bind(DeviceStatusEntity other) {
        super.bind(other);

        this.commSuccessTime = other.commSuccessTime;
        this.commFailedTime = other.commFailedTime;
        this.commFailedCount = other.commFailedCount;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.commFailedCount = (NumberUtils.makeInteger(map.getOrDefault("commFailedCount", 0)));
        this.commSuccessTime = (NumberUtils.makeLong(map.getOrDefault("commSuccessTime", 0L)));
        this.commFailedTime = (NumberUtils.makeLong(map.getOrDefault("commFailedTime", 0L)));
    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            DeviceStatusEntity entity = new DeviceStatusEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
