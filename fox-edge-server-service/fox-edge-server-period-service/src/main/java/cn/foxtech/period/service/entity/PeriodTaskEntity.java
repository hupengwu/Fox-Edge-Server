package cn.foxtech.period.service.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 周期快照任务模板
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PeriodTaskEntity extends PeriodTaskBase {
    /**
     * 配置集合
     */
    private Map<String, Object> taskParam = new HashMap<>();
    /**
     * 对象列表
     */
    private List<Object> deviceIds = new ArrayList<>();
    /**
     * 对象列表
     */
    private List<String> objectIds = new ArrayList<>();


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
