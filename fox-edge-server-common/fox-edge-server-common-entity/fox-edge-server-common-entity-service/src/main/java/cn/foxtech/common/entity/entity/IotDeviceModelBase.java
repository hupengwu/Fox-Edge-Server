package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class IotDeviceModelBase extends BaseEntity {
    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型类型：设备的属性、设备、操作、事件等模型分类，比如设备属性
     */
    private String modelType;

    /**
     * 提供商：定义这个模型的企业，比如华为、腾讯
     */
    private String provider;

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
        list.add(this.modelType);
        list.add(this.provider);
        return list;
    }

    public void bind(IotDeviceModelBase other) {
        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());

        this.modelName = other.modelName;
        this.modelType = other.modelType;
        this.provider = other.provider;
    }
}
