package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DeviceModelEntity extends DeviceModelBase {
    /**
     * 模板参数
     */
    private Map<String, Object> modelParam = new HashMap<>();
    /**
     * 扩展参数
     */
    private Map<String, Object> extendParam = new HashMap<>();

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = super.makeServiceKeyList();

        return list;
    }


    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.modelParam);
        list.add(this.extendParam);

        return list;
    }

    public void bind(DeviceModelEntity other) {
        super.bind(other);

        this.modelParam = other.modelParam;
        this.extendParam = other.extendParam;
    }

    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.modelParam = (Map<String, Object>) map.get("modelParam");
        this.extendParam = (Map<String, Object>) map.get("extendParam");
    }
}