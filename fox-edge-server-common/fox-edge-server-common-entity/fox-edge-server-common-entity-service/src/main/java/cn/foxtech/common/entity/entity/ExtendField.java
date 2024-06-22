package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ExtendField {
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 字段的数据类型
     */
    private String dataType;
    /**
     * 缺省值
     */
    private Object defaultValue;

    public void bind(Map<String, Object> map) {
        this.fieldName = (String) map.get("fieldName");
        this.dataType = (String) map.get("dataType");
        this.defaultValue = map.get("defaultValue");
    }
}
