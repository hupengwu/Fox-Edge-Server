package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_trigger_value")
public class TriggerStatusEntity extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型名
     */
    private String deviceType;

    /**
     * 加工出它的触发器ID
     */
    private Long triggerConfigId;

    /**
     * 配置名称
     */
    private String triggerConfigName;

    /**
     * 数据处理范围
     */
    private String objectRange = "";

    /**
     * 触发器模块名称
     */
    private String triggerModelName = "";
    /**
     * 触发器方法名称
     */
    private String triggerMethodName = "";
    /**
     * 配置集合
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 数值
     */
    private Map<String, DeviceObjectValue> values = new HashMap<>();

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceName);
        list.add(this.triggerConfigId);

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_name", this.deviceName);
        queryWrapper.eq("trigger_config_id", this.triggerConfigId);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.triggerModelName);
        list.add(this.triggerMethodName);
        list.add(this.objectRange);
        list.add(this.params);
        list.add(this.values);
        list.add(this.deviceType);
        return list;
    }
}
