package cn.foxtech.device.protocol.v1.core.annotation;

import java.lang.annotation.*;

/**
 * 标识在解码类上，用于扫描器识别这是为一个自动加载的FoxEdge解码器类型
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeDeviceType {
    /**
     * 设备类型
     *
     * @return
     */
    String value() default "";

    /**
     * 设备版本
     *
     * @return
     */
    String version() default "";

    /**
     * 描述
     *
     * @return
     */
    String description() default "";

    /**
     * 制造商
     *
     * @return
     */
    String manufacturer() default "";
}
