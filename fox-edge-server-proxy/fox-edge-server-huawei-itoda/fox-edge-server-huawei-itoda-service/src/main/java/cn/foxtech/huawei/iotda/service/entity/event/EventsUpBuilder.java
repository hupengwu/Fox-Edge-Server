package cn.foxtech.huawei.iotda.service.entity;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EventsUpBuilder {
    public static final String deviceNamePrefix = "DEVICE-";

    public static void main(String[] args) {
        Date current = Date.from(Instant.now());
        System.out.println(current);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");  // 东京
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(format.format(current));
    }

    public EventsUp add_sub_device_request(String productId, List<Long> deviceIds, String eventId) {
        EventsUp event = new EventsUp();
        event.setObject_device_id("{object_device_id}");

        Service service = new Service();
        event.getServices().add(service);
        service.setService_id("$sub_device_manager");
        service.setEvent_type("add_sub_device_request");
        service.setEvent_time(this.getUTCTime());
        service.setEvent_id(eventId);

        service.getParas().getDevices();
        for (Long deviceId : deviceIds) {
            Device device = new Device();
            device.setName(deviceNamePrefix + deviceId);
            device.setNode_id(deviceNamePrefix + deviceId);
            device.setDescription(deviceNamePrefix + deviceId);
            device.setProduct_id(productId);
        }

        return event;
    }

    public String getUTCTime() {
        Date current = Date.from(Instant.now());

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(current);
    }

}
