package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
}
