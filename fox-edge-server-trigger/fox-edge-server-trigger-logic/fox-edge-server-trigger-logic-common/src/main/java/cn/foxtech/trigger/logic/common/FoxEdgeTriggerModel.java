package cn.foxtech.trigger.logic.common;

import java.lang.annotation.*;

/**
 * 触发器模块
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeTriggerModel {
    /**
     * 触发器名称
     *
     * @return 触发器名称
     */
    String name() default "";

    /**
     * 触发器版本
     *
     * @return 触发器版本
     */
    String version() default "1.0";

    /**
     * 制造商
     *
     * @return 制造商
     */
    String manufacturer() default "";

    /**
     * 触发器描述
     *
     * @return 描述
     */
    String description() default "";
}
