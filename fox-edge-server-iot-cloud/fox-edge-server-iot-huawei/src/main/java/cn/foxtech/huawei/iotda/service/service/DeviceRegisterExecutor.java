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
    public void registerDevice(boolean force) {
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

        if (force){
            // 强制推送全部设备
            this.registerDevices(this.deviceEntityMap.keySet(),key2Entity);
            return;
        }
        else{
            // 动态注册/注销设备
            this.registerDevices(addList,key2Entity);
            this.unregisterDevices(delList,key2Entity);
        }

    }

    private void registerDevices(Set<String> addList, Map<String, DeviceEntity> key2Entity) {
        // 分批发送
        List<List<String>> pages = SplitUtils.split(addList, 40);
        for (List<String> page : pages) {
            // 添加设备，要求的是device的id来构造DEVICE-XXX的设备名称
            List<Long> ids = new ArrayList<>();
            for (String key : page) {
                if (!key2Entity.containsKey(key)){
                    continue;
                }

                ids.add(key2Entity.get(key).getId());
            }

            this.registerDevices(ids);
        }
    }

    private void registerDevices(List<Long> ids) {
        if (ids.isEmpty()){
            return;
        }

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

    private void unregisterDevices(Set<String> delList, Map<String, DeviceEntity> key2Entity) {
        // 分批发送
        List<List<String>> pages = SplitUtils.split(delList, 40);
        for (List<String> page : pages) {
            // 添加设备，要求的是device的id来构造DEVICE-XXX的设备名称
            List<Long> ids = new ArrayList<>();
            for (String key : page) {
                if (!key2Entity.containsKey(key)){
                    continue;
                }

                ids.add(key2Entity.get(key).getId());
            }

            this.unregisterDevices(ids);
        }
    }

    private void unregisterDevices(List<Long> ids) {
        if (ids.isEmpty()){
            return;
        }

        // 分批发送
        String productId = this.huaweiIoTDAService.getProductId();
        String nodeId = this.huaweiIoTDAService.getNodeId();

        // 添加设备，要求的是device的id来构造DEVICE-XXX的设备名称

        // 生成子设备注册事件
        String eventId = UuidUtils.randomUUID();
        EventUp event = EventUpBuilder.delete_sub_device_request(productId, ids, eventId);

        // 转换为JSON报文
        String body = JsonUtils.buildJsonWithoutException(event);

        // 对应的topic
        String topic = EventUpBuilder.getTopic(nodeId);

        // 发送注册请求
        this.remoteMqttService.getClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));
    }
}

