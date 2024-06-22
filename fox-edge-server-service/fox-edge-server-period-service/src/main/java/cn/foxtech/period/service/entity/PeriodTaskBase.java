package cn.foxtech.period.service.entity;

import cn.foxtech.common.entity.entity.BaseEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 周期快照任务模板
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PeriodTaskBase extends BaseEntity {
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 设备厂商
     */
    private String manufacturer;

    /**
     * 是否指定设备
     */
    private Boolean selectDevice;


    /**
     * 业务Key
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
     *
     * @return
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.manufacturer);
        list.add(this.deviceType);

        return list;
    }

    public void bind(PeriodTaskBase other) {
        this.taskName = other.taskName;
        this.manufacturer = other.manufacturer;
        this.deviceType = other.deviceType;
        this.selectDevice = other.selectDevice;

        super.bind(other);
    }
}
