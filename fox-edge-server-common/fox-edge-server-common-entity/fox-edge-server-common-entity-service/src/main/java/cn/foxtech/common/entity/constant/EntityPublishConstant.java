/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.constant;

public class EntityPublishConstant {

    /**
     * 发布模式
     */
    public static final String field_publish_mode = "publishMode";
    /**
     * 数据源类型
     */
    public static final String field_source_type = "sourceType";
    /**
     * 数据源名称
     */
    public static final String field_source_name = "sourceName";
    /**
     * 更新时间
     */
    public static final String field_update_time = "updateTime";
    /**
     * 增量同步方式
     */
    public static final String value_mode_config = "publishConfigMode";
    /**
     * Value只记录DeviceValue和TriggerValue的数据记录，它的变化非常高速，它在云端只是镜像副本数据
     * 工作过程：每次进行全量同步
     * 1、比对本地和云端的时间戳，判定是否需要进行同步，如果需要同步，就进行后面的流程
     * 2、向云端发出重置操作，云端接收到这个请求后，会清空自己的表数据
     * 3、向云端循环的分页提交本地mysql的全部数据，云端会将数据逐个的插入到自己的表总
     * 4、向云端发出完成操作，云端接收到这个操作后，会标识同步状态为完成
     * 5、至此，两边数据同步结束，重新等待本地的数据和时间戳发生变化，然后重新进行上述流程
     */
    public static final String value_mode_value = "publishValueMode";
    /**
     * Object只记录DeviceObject和TriggerObject的数据记录，它的重要性很高，它在云端只是镜像副本数据
     * 工作过程：每次进行全量同步
     * 1、比对本地和云端的时间戳，判定是否需要进行同步，如果需要同步，就进行后面的流程
     * 2、向云端发出重置操作，云端接收到这个请求后，会清空自己的表数据
     * 3、向云端循环的分页提交本地mysql的全部数据，云端会将数据逐个的插入到自己的表总
     * 4、向云端发出完成操作，云端接收到这个操作后，会标识同步状态为完成
     * 5、至此，两边数据同步结束，重新等待本地的数据和时间戳发生变化，然后重新进行上述流程
     */
    public static final String value_mode_define = "publishDefineObject";

    /**
     * MySql记录数据的发布模式：对mysql中的记录数据，按记录方式进行发布
     */
    public static final String value_mode_record = "publishRecordMode";

    /**
     * MySql日志数据的发布模式：对mysql中的日志数据，按记录方式进行发布
     */
    public static final String value_mode_logger = "publishLoggerMode";

    /**
     * 数据源类型：redis在本地的镜像缓存，指明数据存放再本地缓存当中
     */
    public static final String value_type_cache = "cache";
    /**
     * 数据源类型：redis缓存，指明数据存放再redis缓存当中
     */
    public static final String value_type_redis = "redis";
    /**
     * 数据源类型：mysql，指明数据源存放再mysql的表当中
     */
    public static final String value_type_mysql = "mysql";
}
