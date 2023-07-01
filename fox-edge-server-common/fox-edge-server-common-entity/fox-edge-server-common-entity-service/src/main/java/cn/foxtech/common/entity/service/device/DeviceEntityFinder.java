package cn.foxtech.common.entity.service.device;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.service.redis.IBaseFinder;
import lombok.AccessLevel;
import lombok.Setter;

@Setter(value = AccessLevel.PUBLIC)
public class DeviceEntityFinder implements IBaseFinder {
    /**
     * 通道名称：连接设备的服务名，比如"serialport"
     */
    private String channelType;

    /**
     * 通道名称：连接设备的串口名，比如"COM1"
     */
    private String channelName;

    public boolean compareValue(Object value) {
        DeviceEntity deviceEntity = (DeviceEntity) value;
        if (channelName != null || !channelName.equals(deviceEntity.getDeviceName())) {
            return false;
        }
        if (channelType != null || !channelType.equals(deviceEntity.getChannelType())) {
            return false;
        }
        return true;
    }
}
