package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.huawei.iotda.service.entity.event.EventUp;
import cn.foxtech.huawei.iotda.service.entity.event.EventUpBuilder;
import cn.foxtech.huawei.iotda.service.huawei.HuaweiIoTDAService;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 向华为平台，注册设备
 */
@Component
public class DeviceRegisterExecutor {
    private final Map<String, DeviceEntity> deviceEntityMap = new ConcurrentHashMap<>();
    @Autowired
    private RemoteMqttService remoteMqttService;
    @Autowired
    private DeviceExecutor deviceExecutor;
    @Autowired
    private HuaweiIoTDAService huaweiIoTDAService;

    /**
     * 添加全部设备
     */
    public void registerDevice() {
        // 获得华为物联网平台相关的设备
        Map<String, DeviceEntity> key2Entity = this.deviceExecutor.getKey2Entity();

        // 通过比较，确定设备的变化
        Set<String> addList = new HashSet<String>();
        Set<String> delList = new HashSet<String>();
        Set<String> eqlList = new HashSet<String>();
        DifferUtils.differByValue(this.deviceEntityMap.keySet(), key2Entity.keySet(), addList, delList, eqlList);

        // 保存新增的设备
        for (String key : addList) {
            this.deviceEntityMap.put(key, key2Entity.get(key));
        }

        // 分批发送
        List<List<String>> pages = SplitUtils.split(addList, 40);
        for (List<String> page : pages) {
            // 添加设备，要求的是device的id来构造DEVICE-XXX的设备名称
            List<Long> ids = new ArrayList<>();
            for (String key : page) {
                ids.add(key2Entity.get(key).getId());
            }

            this.registerDevices(ids);
        }
    }

    /**
     * 添加单个设备
     *
     * @param deviceEntity 设备实体
     */
    public void registerDevice(DeviceEntity deviceEntity) {
        // 添加设备，要求的是device的id来构造DEVICE-XXX的设备名称
        List<Long> ids = new ArrayList<>();
        ids.add(deviceEntity.getId());

        this.registerDevices(ids);
    }

    private void registerDevices(List<Long> ids) {
        // 分批发送
        String productId = this.huaweiIoTDAService.getProductId();
        String nodeId = this.huaweiIoTDAService.getNodeId();
        String deviceId = this.huaweiIoTDAService.getDeviceId();

        // 添加设备，要求的是device的id来构造DEVICE-XXX的设备名称

        // 生成子设备注册事件
        String eventId = UuidUtils.randomUUID();
        EventUp event = EventUpBuilder.add_sub_device_request(productId, ids, eventId);

        // 转换为JSON报文
        String body = JsonUtils.buildJsonWithoutException(event);

        // 对应的topic
        String topic = EventUpBuilder.getTopic(nodeId);

        // 发送注册请求
        this.remoteMqttService.getClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));
    }
}

