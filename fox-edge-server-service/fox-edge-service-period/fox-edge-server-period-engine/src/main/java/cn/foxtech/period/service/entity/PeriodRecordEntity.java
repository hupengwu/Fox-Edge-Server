package cn.foxtech.period.service.entity;


import cn.foxtech.common.entity.entity.LogEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_period_record")
public class PeriodRecordEntity extends LogEntity {
    /**
     * 任务名称
     */
    private Long taskId;

    /**
     * 设备类型名
     */
    private String recordBatch;
    /**
     * 对象名称
     */
    private Long deviceId;
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 对象数值
     */
    private Object objectValue;

    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.taskId);
        list.add(this.recordBatch);
        list.add(this.deviceId);
        list.add(this.objectName);


        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_name", this.taskId);
        queryWrapper.eq("device_id", this.deviceId);
        queryWrapper.eq("object_name", this.objectName);
        queryWrapper.eq("record_batch", this.recordBatch);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.objectValue);
        return list;
    }

    public void bind(PeriodRecordEntity other) {
        this.taskId = other.taskId;
        this.deviceId = other.deviceId;
        this.objectName = other.objectName;
        this.recordBatch = other.recordBatch;
        this.objectValue = other.objectValue;

        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
    }
}
