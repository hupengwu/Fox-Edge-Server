package cn.foxtech.channel.bacnet.server.entity;

import cn.foxtech.common.utils.json.JsonUtils;
import com.serotonin.bacnet4j.LocalDevice;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;

/**
 * 数据实体
 */
@Data
public class BACnetDataEntity {
    /**
     * 本地虚拟设备对象
     */
    private LocalDevice localDevice;

    /**
     * 本地虚拟设备的IP
     */
    private String localDeviceIp = "192.168.1.3";
    /**
     * 本地虚拟设备的端口
     */
    private int localDevicePort = 47808;
    /**
     * 本地虚拟设备的ID
     */
    private int localDeviceId = 9547;

    /**
     * 发现远端设备需要的等待时间
     */
    private int discoveryTime = 3000;


    public void build(String json) throws IOException {
        JBACNetConfig jsnData = JsonUtils.buildObject(json, JBACNetConfig.class);
        this.localDeviceId = jsnData.local_device.device_id;
        this.localDeviceIp = jsnData.local_device.ip;
        this.localDevicePort = jsnData.local_device.port;
        this.discoveryTime = jsnData.local_device.discovery_time;
    }

    @Data
    static class JBACNetConfig implements Serializable {
        private JLocalDevice local_device = new JLocalDevice();
    }

    @Data
    static class JLocalDevice implements Serializable {
        private String ip;
        private int port;
        private int device_id;
        private int discovery_time;
    }
}
