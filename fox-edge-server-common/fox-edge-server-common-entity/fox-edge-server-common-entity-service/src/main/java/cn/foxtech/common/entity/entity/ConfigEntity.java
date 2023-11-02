package cn.foxtech.common.entity.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置实体：各服务需要读取预置的全局配置参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ConfigEntity extends ConfigBase {
    /**
     * 数值信息
     */
    private Map<String, Object> configValue = new HashMap<>();
    /**
     * 参数信息
     */
    private Map<String, Object> configParam = new HashMap<>();

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
        list.add(this.configValue);
        list.add(this.configParam);

        return list;
    }

    public void bind(ConfigEntity other) {
        super.bind(other);

        this.configValue = other.configValue;
        this.configParam = other.configParam;
    }

    public ConfigEntity clone() {
        ConfigEntity clone = new ConfigEntity();

        clone.bind(this);

        clone.configValue = new HashMap<>();
        clone.configParam = new HashMap<>();

        clone.configValue.putAll(this.configValue);
        clone.configParam.putAll(this.configParam);

        return clone;
    }
}
