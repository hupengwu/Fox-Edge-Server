package cn.foxtech.huawei.iotda.service.entity.event;

import cn.foxtech.huawei.iotda.service.entity.utils.TimeUtils;

import java.util.List;
import java.util.Map;

public class EventUpBuilder {
    public static final String deviceNamePrefix = "DEVICE-";

    public static EventUp add_sub_device_request(String productId, List<Long> deviceIds, String eventId) {
        EventUp event = new EventUp();
        event.setObject_device_id("{object_device_id}");

        Service service = new Service();
        event.getServices().add(service);
        service.setService_id("$sub_device_manager");
        service.setEvent_type("add_sub_device_request");
        service.setEvent_time(TimeUtils.getUTCTime());
        service.setEvent_id(eventId);
        service.setParas(new SubDevRegisterParas());

        for (Long deviceId : deviceIds) {
            Device device = new Device();
            device.setName(deviceNamePrefix + deviceId);
            device.setNode_id(deviceNamePrefix + deviceId);
            device.setDescription(deviceNamePrefix + deviceId);
            device.setProduct_id(productId);

            SubDevRegisterParas subDevRegisterParas = (SubDevRegisterParas) service.getParas();
            subDevRegisterParas.getDevices().add(device);
        }

        return event;
    }

    public static EventUp sub_device_update_status(String productId, Map<Long, Boolean> deviceStatuss, String eventId) {
        EventUp event = new EventUp();
        event.setObject_device_id("{object_device_id}");

        Service service = new Service();
        event.getServices().add(service);
        service.setService_id("$sub_device_manager");
        service.setEvent_type("sub_device_update_status");
        service.setEvent_time(TimeUtils.getUTCTime());
        service.setEvent_id(eventId);
        service.setParas(new SubDevOnlineParas());

        for (Long deviceId : deviceStatuss.keySet()) {
            DeviceStatus deviceStatus = new DeviceStatus();
            deviceStatus.setDevice_id(getSubDevId(productId, deviceId));
            if (Boolean.TRUE.equals(deviceStatuss.get(deviceId))) {
                deviceStatus.setStatus("ONLINE");
            } else {
                deviceStatus.setStatus("OFFLINE");
            }


            SubDevOnlineParas subDevOnlineParas = (SubDevOnlineParas) service.getParas();
            subDevOnlineParas.getDevice_statuses().add(deviceStatus);
        }

        return event;
    }

    public static String getSubDevId(String productId, Long deviceId) {
        return productId + "_" + deviceNamePrefix + deviceId;
    }

    public static String getTopic(String nodeId) {
        return "$oc/devices/" + nodeId + "/sys/events/up";
    }


}
