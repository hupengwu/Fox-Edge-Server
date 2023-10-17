package cn.foxtech.channel.opcua.entity;

import cn.foxtech.channel.opcua.handler.OpcUaHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaChannelEntity {
    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 配置参数
     */
    private OpcUaConfigEntity opcConfig;

    /**
     * OpcUaClient代表的是远端的设备
     */
    private OpcUaClient opcUaClient;

    /**
     * UaClient代表的是跟远端设备的一个连接
     */
    private UaClient opcLink;

    /**
     * OpcUaHandler代表的是对跟远端设备的一个连接的监听线程
     */
    private OpcUaHandler handler;

    /**
     * 激活时间
     */
    private Long activeTime = 0L;
}
