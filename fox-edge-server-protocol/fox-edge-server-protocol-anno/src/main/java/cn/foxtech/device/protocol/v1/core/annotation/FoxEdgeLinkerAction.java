package cn.foxtech.device.protocol.v1.core.annotation;

import java.lang.annotation.*;

/**
 * 链路状态注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeLinkerAction {
    public static final String CREATE_LINKER_REQUEST = "createLinkerRequest";
    public static final String CREATE_LINKER_RESPOND = "createLinkerRespond";
    public static final String ACTIVE_LINKER_REQUEST = "activeLinkerRequest";
    public static final String ACTIVE_LINKER_RESPOND = "activeLinkerRespond";
    public static final String INTERCEPT_LINKER_RESPOND = "interceptLinkerRespond";

    /**
     * 设备类型
     *
     * @return
     */
    String value() default "";
}
