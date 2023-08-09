package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TriggerConfigBase extends BaseEntity {
    public static final String GlobalLevel = "Global";
    public static final String DeviceLevel = "Device";

    /**
     * 范围级别
     */
    private String objectRange = "";

    private String deviceName = "";
    /**
     * 对象类型信息：deviceType和objectsName是必填参数
     */
    private String deviceType = "";

    /**
     * 触发器信息
     */
    private String triggerConfigName = "";
    private String triggerModelName = "";
    private String triggerMethodName = "";

    /**
     * 队列深度
     */
    private Integer queueDeep = 1;


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
        list.add(this.objectRange);
        list.add(this.deviceName);
        list.add(this.deviceType);
        list.add(this.triggerConfigName);
        list.add(this.triggerModelName);
        list.add(this.triggerMethodName);
        list.add(this.queueDeep);

        return list;
    }
}
