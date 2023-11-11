package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateManualTaskBase extends BaseEntity {
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备类型名
     */
    private String deviceType;
    /**
     * 设备厂商
     */
    private String manufacturer;

    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.taskName);


        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_name", this.taskName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceName);
        list.add(this.deviceType);
        list.add(this.manufacturer);
        return list;
    }

    public void bind(OperateManualTaskBase other) {
        this.taskName = other.taskName;
        this.deviceName = other.deviceName;
        this.deviceType = other.deviceType;
        this.manufacturer = other.manufacturer;


        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());
    }
}
