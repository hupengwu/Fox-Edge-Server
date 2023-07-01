package cn.foxtech.device.protocol.core.annotation;

import java.lang.annotation.*;

/**
 * 标识在编码和解码函数上，用于扫描器识别需要用户输入什么参数给它
 *   names和values的数组长度必须按key-value关系对应
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeOperateParam {
    /**
     * 输入参数名称
     */
    String[] names() default {};

    /**
     * 输入参数的值
     */
    String[] values() default {};
}
