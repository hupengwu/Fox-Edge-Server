package cn.foxtech.huawei.iotda.service.entity.utils;

public class ServiceIdUtils {
    public static String getServiceId(String deviceType) {
        deviceType = deviceType.replace("+", "_");
        deviceType = deviceType.replace(" ", "_");
        return deviceType;
    }
}
