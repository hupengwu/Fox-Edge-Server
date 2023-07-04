package cn.foxtech.period.service.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

/**
 * 周期快照任务模板
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_period_task")
public class PeriodTaskPo extends PeriodTaskBase {
    /**
     * 配置参数
     */
    private String taskParam;

    /**
     * 设备列表
     */
    private String deviceIds;

    /**
     * 对象列表
     */
    private String objectIds;

    /**
     * 获取业务值
     *
     * @return
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.taskParam);
        list.add(this.deviceIds);
        list.add(this.objectIds);

        return list;
    }
}
