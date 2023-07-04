package cn.foxtech.trigger.logic.common;

import java.lang.annotation.*;

/**
 * 触发器函数
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeTriggerMethod {
    /**
     * 名称
     *
     * @return 名称
     */
    String name() default "";

    /**
     * 触发器描述
     *
     * @return
     */
    String description() default "";
}
