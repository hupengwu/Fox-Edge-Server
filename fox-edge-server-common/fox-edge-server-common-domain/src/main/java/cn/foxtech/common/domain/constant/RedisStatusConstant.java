/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.domain.constant;

public class RedisStatusConstant {
    /**
     * 业务名称：也就是应用名称，比如manager-service
     */
    public static final String field_service_type = "serviceType";

    /**
     * 业务类型：也就是 kernel/service/system
     */
    public static final String field_service_name = "serviceName";

    /**
     * 模块类型：也就是 channel/controller/device/persist
     */
    public static final String field_model_type = "modelType";

    /**
     * 模块名称：也就是serialport/simulator这类名，方便进程之间互相识别业务身份
     */
    public static final String field_model_name = "modelName";
    /**
     * 程序名称：spring的应用名，例如fox-edge-server-channel-simulator-service
     */
    public static final String field_application_name = "applicationName";
    /**
     * 激活时间：程序心跳时间，告知别的服务，自己还在工作
     */
    public static final String field_active_time = "activeTime";

    /**
     * 允许进行option操作的数据实体信息
     */
    public static final String field_option_entity = "optionEntity";

    /**
     * 发布数据：这是一个Map结构，告诉cloud publish服务，它有哪些实体数据，并以什么方式来发布
     */
    public static final String field_publish_entity = "publishEntity";

    /**
     * 配置数据：这是一个Map结构，告诉system-manage服务，它有哪些数据，作为初始化配置，放到它的configEntity中
     */
    public static final String field_config_entity = "configEntity";
    public static final String value_model_type_channel = "channel";
    public static final String value_model_type_device = "device";
    public static final String value_model_name_device = "device";
}
