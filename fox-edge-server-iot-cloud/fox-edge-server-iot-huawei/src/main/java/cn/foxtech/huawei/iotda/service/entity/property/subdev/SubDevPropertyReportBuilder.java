package cn.foxtech.huawei.iotda.service.entity.property.subdev;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.huawei.iotda.service.entity.property.Service;
import cn.foxtech.huawei.iotda.service.entity.utils.ServiceIdUtils;
import cn.foxtech.huawei.iotda.service.entity.utils.TimeUtils;

import java.util.Map;

public class SubDevPropertyReportBuilder {
    public static final String deviceNamePrefix = "DEVICE-";

    public static String getSubDevId(String productId, Long deviceId) {
        return productId + "_" + deviceNamePrefix + deviceId;
    }

    public static SubDevPropertyReport sub_devices_property_report_request(Map<String, Map<String, Object>> deviceValueMap, String productId,Map<String, Map<String, Object>> modelMap, Map<String, DeviceEntity> key2Entity) {
        SubDevPropertyReport report = new SubDevPropertyReport();
        for (String deviceKey : deviceValueMap.keySet()) {
            Map<String, Object> valueMap = deviceValueMap.get(deviceKey);
            DeviceEntity deviceEntity = key2Entity.get(deviceKey);
            if (deviceEntity == null) {
                continue;
            }

            Device device = new Device();
            device.setDevice_id(getSubDevId(productId, deviceEntity.getId()));


            // 数值数据，全部作为analog
            Service service = new Service();
            service.setService_id(ServiceIdUtils.getServiceId(deviceEntity.getDeviceType()));
            service.getProperties().putAll(valueMap);
            service.setEvent_time(TimeUtils.getUTCTime());
            device.getServices().add(service);


            report.getDevices().add(device);
        }

        return report;
    }

    public static String getTopic(String nodeId) {
        return "$oc/devices/" + nodeId + "/sys/gateway/sub_devices/properties/report";
    }
}
