package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TriggerMethodEntity extends BaseEntity {

    /**
     * 模块名称
     */
    private String modelName;

    /**
     * 函数名称
     */
    private String methodName;

    /**
     * 开发者
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
        list.add(this.methodName);
        list.add(this.manufacturer);

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
        queryWrapper.eq("method_name", this.methodName);
        queryWrapper.eq("manufacturer", this.manufacturer);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        return list;
    }

    public void bind(TriggerMethodEntity other) {
        this.manufacturer = other.manufacturer;
        this.modelName = other.modelName;
        this.methodName = other.methodName;

        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());
    }
}
