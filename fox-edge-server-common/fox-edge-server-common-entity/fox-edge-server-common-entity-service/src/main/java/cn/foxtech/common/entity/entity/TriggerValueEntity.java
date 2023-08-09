package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.List;

/**
 * 触发值，跟设备值同构，但是它的生产者是触发器，而不是控制器
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_trigger_value")
public class TriggerValueEntity extends DeviceValueEntity {
    /**
     * 触发器配置名称
     */
    private String triggerConfigName;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = super.makeServiceKeyList();
        list.add(this.triggerConfigName);

        return list;
    }

    /**
     * 获取业务值
     *
     * @return 对象实体
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_name", this.getDeviceName());
        queryWrapper.eq("trigger_config_name", this.triggerConfigName);

        return queryWrapper;
    }

    public void bingEntity(DeviceValueEntity deviceValueEntity) {
        this.setDeviceName(deviceValueEntity.getDeviceName());
        this.setParams(deviceValueEntity.getParams());
        this.setDeviceType(deviceValueEntity.getDeviceType());
        this.setCreateTime(deviceValueEntity.getCreateTime());
        this.setId(deviceValueEntity.getId());
        this.setUpdateTime(deviceValueEntity.getUpdateTime());
    }

}
