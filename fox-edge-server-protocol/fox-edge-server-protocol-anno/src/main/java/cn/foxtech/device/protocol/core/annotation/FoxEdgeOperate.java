package cn.foxtech.device.protocol.core.annotation;

import java.lang.annotation.*;

/**
 * 编码器/解码器注解：标识在解码函数上，用于扫描器识别出这是为一个自动加载的FoxEdge编码器/解码器函数
 * 这是基本注解：report注解、publish注解都是依赖于这个基础注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeOperate {
    /**
     * 编码器类型
     */
    public static final String encoder = "encoder";
    /**
     * 解码器类型
     */
    public static final String decoder = "decoder";

    /**
     * 模式：它决定了返回的消息结构是状态类型的，还是记录类型的
     */
    public static final String mode = "mode";

    /**
     * 状态模式：比如温度、湿度，一个对象，数值数据来回变化更新，依然只有一个数据对象，只是反复刷新
     * 存储方式：这种数据应当作为状态变量，被更新到redis的状态表当中
     */
    public static final String status = "status";

    /**
     * 记录模式：比如门禁的员工打卡记录，一个对象，每天来回产生大量的不同数据记录，它会产生很多记录
     * 存储方式：这种数据应当作为用户记录，被更新到mysql的设备记录表中
     */
    public static final String record = "record";

    /**
     * 确认模式：用户开关某个信号，设备会应答确认/拒绝，这种信息仅仅对这个会话有效，并不应该产生记录
     * 存储方式：这种数据应当作为会话操作结果，被记录到mysql的用户操作记录表中，或者不保存操作记录
     */
    public static final String result = "result";


    /**
     * 名称
     *
     * @return 名称
     */
    String name() default "";

    /**
     * 最大通信超时
     *
     * @return 最大通信超时
     */
    int timeout() default 2000;

    /**
     * 是否轮询
     *
     * @return 是否轮询
     */
    boolean polling() default false;

    /**
     * 类型：解码器/编码器
     *
     * @return 类型
     */
    String type() default encoder;

    /**
     * 模式：状态模式/记录模式
     *
     * @return 模式
     */
    String mode() default status;
}
