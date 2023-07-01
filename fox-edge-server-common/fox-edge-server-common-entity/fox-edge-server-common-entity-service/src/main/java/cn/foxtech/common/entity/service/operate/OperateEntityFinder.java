package cn.foxtech.common.entity.service.operate;

import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.service.redis.IBaseFinder;

public class OperateEntityFinder implements IBaseFinder {
    private final String deviceType;

    public OperateEntityFinder(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean compareValue(Object value) {
        return deviceType.equals(((OperateEntity) value).getDeviceType());
    }
}
