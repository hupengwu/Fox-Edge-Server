package cn.foxtech.device.domain.constant;

public class DeviceMethodVOFieldConstant {
    public static final String field_client_name = "clientName";
    public static final String field_uuid = "uuid";
    public static final String field_timeout = "timeout";

    // 接收的topic名称
    public static final String field_route = "route";


    /**
     * 字段值定义：field_operate
     */
    public static final String value_operate_exchange = "exchange";// 主从半双工：跟设备交换数据
    public static final String value_operate_publish = "publish";//对设备单向操作
    public static final String value_operate_report = "report";// 设备对服务器单向上报

    public static final String value_data_value = "value";//data->value
}
