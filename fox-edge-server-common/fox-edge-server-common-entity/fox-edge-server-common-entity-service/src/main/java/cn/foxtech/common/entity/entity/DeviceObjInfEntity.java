package cn.foxtech.common.entity.entity;


import cn.foxtech.common.utils.number.NumberUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 设备对象的结构化信息：用来形成某个设备类型级别的数据结构
 * <p>
 * 说明：持久化服务在持续收到设备数值的时候，会将设备数值的信息提取出来，合并到对象信息实体中
 * 这样就形成了设备类型级别的信息，类似数据库的distinct操作，但是这个只在redis/缓存中，性能高
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceObjInfEntity extends BaseEntity {
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 设备参数
     */
    private String manufacturer;
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 对象名称
     */
    private String valueType;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.manufacturer);
        list.add(this.deviceType);
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
        queryWrapper.eq("device_type", this.deviceType);
        queryWrapper.eq("manufacturer", this.manufacturer);
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
        list.add(this.valueType);

        return list;
    }

    public void bind(DeviceObjInfEntity other) {
        this.deviceType = other.deviceType;
        this.manufacturer = other.manufacturer;
        this.objectName = other.objectName;
        this.valueType = other.valueType;


        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());
    }

    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            DeviceObjInfEntity entity = new DeviceObjInfEntity();
            entity.setId(NumberUtils.makeLong(map.get("id")));
            entity.setCreateTime(NumberUtils.makeLong(map.get("createTime")));
            entity.setUpdateTime(NumberUtils.makeLong(map.get("updateTime")));


            entity.setDeviceType((String) map.get("deviceType"));
            entity.setManufacturer((String) map.get("manufacturer"));
            entity.setObjectName((String) map.get("objectName"));
            entity.setValueType((String) map.get("valueType"));

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
