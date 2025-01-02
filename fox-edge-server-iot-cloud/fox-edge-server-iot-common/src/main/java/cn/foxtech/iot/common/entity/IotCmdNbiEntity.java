package cn.foxtech.iot.common.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class IotCmdNbiEntity {
    /**
     * 必填参数：在云端定义的参数key
     */
    private String paraKey;
    /**
     * 必填参数：数据key
     */
    private String dataKey;
    /**
     * 必填参数：数据类型
     */
    private String dataType;
    /**
     * 选填参数：当dataType为string的时候，可进行编码格式的转换
     */
    private String paraCode;
    /**
     * 选填参数：当dataType为string的时候，可进行编码格式的转换
     */
    private String dataCode;
    /**
     * 选填参数：当dataType为enum的时候，可进行枚举值的转换
     */
    private Map<Object, Object> enumPara;

}
