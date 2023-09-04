package cn.foxtech.device.protocol.v1.core.channel;

public interface FoxEdgeChannelService {
    /**
     * 数据交换操作：一问一答方式的主从半双工通信方式
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @param send       发送的数据
     * @param timeout    通信超时时间间隔
     * @return           返回的报文内容
     * @throws Exception 通信异常信息
     */
    Object exchange(String deviceName, String deviceType, Object send, int timeout) throws Exception;

    /**
     *
     * 数据发布操作：单向发布到设备的通信方式
     *
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @param send       发送的数据
     * @param timeout  发送超时
     * @throws Exception 通信异常信息
     */
    void publish(String deviceName, String deviceType, Object send, int timeout) throws Exception;
}
