package cn.foxtech.device.protocol.v1.core.annotation;

import java.lang.annotation.*;

/**
 * 事件注解：某些设备支持的日志、通知
 * 它需要依赖FoxEdgeOperate注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeReport {
    /**
     * 告警类数据：上位机读取设备数据后，设备侧的事件就被取走了，设备自己并不保留。
     * 如果上位机取走后，把它丢了，那么就没有这个事件了，比如很多简单设备的告警通知，就是如此设计的。
     * 告警数据，在现实中通常被设备厂家设计出来，简单的通知其他设备或者上位机。
     */
    String alarm = "alarm";

    /**
     * 事件类数据：上位机读取设备数据后，设备侧的事件依然保留，需要上位机主动删除，设备侧才会删除。
     * 可靠性比较高，比如很多设备的事件表，上位机可以读取，读取后可以通知设备删除
     * 事件类数据，在现实中通常被设备厂家设计出来，跟其他系统或者设备进行可靠性的交互。
     */
    String event = "event";

    /**
     * 日志类数据：设备的日志数据是它自己维护的，上位机只有读取的能力，设备没有容量空间了才会定期删除。
     * 可靠性非常高，比如很多高端设备内部有自己的日志记录表，方便维护人员定位问题。
     * 日志类数据，在现实中通常被厂家设计出来，给开发人员和维护人员进行问题的定位分享。
     */
    String logs = "logs";

    /**
     * 类型：事件类型
     *
     * @return
     */
    String type() default logs;
}
