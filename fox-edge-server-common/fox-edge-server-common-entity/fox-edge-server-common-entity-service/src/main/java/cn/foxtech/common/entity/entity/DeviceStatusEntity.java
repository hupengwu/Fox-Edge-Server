package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


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
        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());

        this.commSuccessTime = other.commSuccessTime;
        this.commFailedTime = other.commFailedTime;
        this.commFailedCount = other.commFailedCount;
    }
}
