package cn.foxtech.trigger.logic.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class FoxEdgeTrigger {
    /**
     * 触发器模块名称
     */
    private String modelName;
    /**
     * 触发器函数名称
     */
    private String methodName;
    /**
     * 开发厂商名称
     */
    private String manufacturer;

    /**
     * 触发器函数
     */
    private Method method;
}
