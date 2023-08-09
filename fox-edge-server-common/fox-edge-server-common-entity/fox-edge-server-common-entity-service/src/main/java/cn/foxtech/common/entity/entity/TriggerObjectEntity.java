package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_trigger_object")
public class TriggerObjectEntity extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 触发器配置名称
     */
    private String triggerConfigName;
    /**
     * 对象名称
     */
    private String objectName;


    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.triggerConfigName);
        list.add(this.deviceName);
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
        queryWrapper.eq("trigger_config_name", this.triggerConfigName);
        queryWrapper.eq("device_name", this.deviceName);
        queryWrapper.eq("object_name", this.objectName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceType);
        return list;
    }

    public void bind(TriggerObjectEntity other) {
        this.deviceType = other.deviceType;
        this.deviceName = other.deviceName;
        this.objectName = other.objectName;
        this.triggerConfigName = other.triggerConfigName;


        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());
    }
}
