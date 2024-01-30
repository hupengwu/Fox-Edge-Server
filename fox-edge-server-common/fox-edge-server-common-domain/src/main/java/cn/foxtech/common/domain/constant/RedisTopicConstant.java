package cn.foxtech.common.domain.constant;

public class RedisTopicConstant {
    /**
     * 全体同类模块
     */
    public static final String model_public = "public";


    /**
     * 设备模块
     */
    public static final String model_channel = "channel";
    /**
     * 设备模块
     */
    public static final String model_device = "device";
    /**
     * 公共模块
     */
    public static final String model_manager = "manager";

    /**
     * 持久化模块：将设备数据保存到数据库的模块
     */
    public static final String model_persist = "persist";


    /**
     * 代理模块
     */
    public static final String model_api = "api";

    /**
     * 代理模块
     */
    public static final String model_proxy4cloud2topic = "proxy4cloud2topic";


    /**
     * topic_xxx_request_yyy：xxx代表的是责任主体，yyy代表的是相关的主体
     * 比如topic_channel_request_manager，channel这个责任主体，有数据发给manager这个相关主体
     *
     * 有下划线，代表会有多个使用该前缀为topic，无下划线代表只有一个topic
     */

    public static final String topic_link_request = "topic_link_request_";
    public static final String topic_link_respond = "topic_link_respond_";

    public static final String topic_channel_request = "topic_channel_request_";
    public static final String topic_channel_respond = "topic_channel_respond_";

    public static final String topic_device_request = "topic_device_request_";
    public static final String topic_device_respond = "topic_device_respond_";

    public static final String topic_persist_request = "topic_persist_request_";
    public static final String topic_persist_respond = "topic_persist_respond_";

    public static final String topic_trigger_request = "topic_trigger_request_";
    public static final String topic_trigger_respond = "topic_trigger_respond_";

    public static final String topic_manager_request = "topic_manager_request_";
    public static final String topic_manager_respond = "topic_manager_respond_";

    public static final String topic_gateway_request = "topic_gateway_request";
    public static final String topic_gateway_respond = "topic_gateway_respond";
}
