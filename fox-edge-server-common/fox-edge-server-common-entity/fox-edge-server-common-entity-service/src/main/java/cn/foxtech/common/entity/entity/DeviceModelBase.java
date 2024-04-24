package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceModelBase extends BaseEntity {
    /**
     * 模板名称
     */
    private String modelName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备厂商
     */
    private String manufacturer;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.modelName);

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model_name", this.modelName);

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
        list.add(this.manufacturer);
        return list;
    }

    public void bind(DeviceModelBase other) {
        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());

        this.modelName = other.modelName;
        this.deviceType = other.deviceType;
        this.manufacturer = other.manufacturer;
    }

    public void bind(Map<String, Object> map){
        this.setId(Long.parseLong(map.getOrDefault("id","0").toString()));
        this.setCreateTime(Long.parseLong(map.getOrDefault("createTime","0").toString()));
        this.setUpdateTime(Long.parseLong(map.getOrDefault("updateTime","0").toString()));

        this.manufacturer = (String)map.get("manufacturer");
        this.deviceType = (String)map.get("deviceType");
        this.modelName = (String)map.get("modelName");
    }
}
