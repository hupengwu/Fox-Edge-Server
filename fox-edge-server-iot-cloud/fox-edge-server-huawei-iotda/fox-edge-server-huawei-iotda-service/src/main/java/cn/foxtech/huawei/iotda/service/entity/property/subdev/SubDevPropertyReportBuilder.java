package cn.foxtech.huawei.iotda.service.entity.property.subdev;

import cn.foxtech.huawei.iotda.service.entity.property.Service;
import cn.foxtech.huawei.iotda.service.entity.utils.TimeUtils;

import java.util.Map;

public class SubDevPropertyReportBuilder {
    public static final String deviceNamePrefix = "DEVICE-";

    public static String getSubDevId(String productId, Long deviceId) {
        return productId + "_" + deviceNamePrefix + deviceId;
    }

    public static SubDevPropertyReport sub_devices_property_report_request(String productId, String ServiceId, Map<Long, Map<String, Object>> deviceValueMap) {
        SubDevPropertyReport report = new SubDevPropertyReport();
        for (Long deviceId : deviceValueMap.keySet()) {
            Map<String, Object> valueMap = deviceValueMap.get(deviceId);

            Device device = new Device();
            device.setDevice_id(getSubDevId(productId, deviceId));

            // 数值数据，全部作为analog
            Service service = new Service();
            service.setService_id(ServiceId);
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
