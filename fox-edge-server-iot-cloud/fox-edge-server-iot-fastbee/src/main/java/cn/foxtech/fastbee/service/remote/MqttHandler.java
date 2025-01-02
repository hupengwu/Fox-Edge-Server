/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.fastbee.service.remote;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.mqtt.MqttClientHandler;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.fastbee.service.service.FastBeeService;
import cn.foxtech.iot.common.service.EntityManageService;
import cn.foxtech.iot.common.service.IotCmdDictService;
import cn.foxtech.rpc.sdk.redis.RpcSdkRedisClient;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class MqttHandler extends MqttClientHandler {
    @Setter
    @Getter
    private String topic;

    @Autowired
    private IotCmdDictService dictService;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private FastBeeService fastBeeService;

    @Autowired
    private RpcSdkRedisClient redisClient;

    @Autowired
    private ServiceStatus serviceStatus;

    @Autowired
    private RedisConsoleService consoleService;

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, byte[] payload) {
        String json = new String(payload, StandardCharsets.UTF_8);

        if (!topic.endsWith("/function/get")) {
            return;
        }

        if (MethodUtils.hasEmpty(json)) {
            return;
        }

        this.respond(topic, json);
    }

    private void respond(String topic, String json) {
        try {
            // 解析topic的格式：/{productId}/{deviceNum}/function/get
            String[] items = topic.split("/");
            if (items.length < 3) {
                return;
            }
            String productId = items[1];
            String deviceNum = items[2];

            // 检查：输入参数是否有效
            if (MethodUtils.hasEmpty(productId, deviceNum, json)) {
                return;
            }

            // 找出对应的DeviceEntity
            DeviceEntity deviceEntity = this.getDeviceEntity(productId, deviceNum);
            if (deviceEntity == null) {
                return;
            }

            // 通过时间戳，判断设备服务是否正在运行
            if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_device, RedisStatusConstant.value_model_name_device, 60 * 1000)) {
                return;
            }

            // 将操作请求，提交给设备服务
            List<Map<String, Object>> list = JsonUtils.buildObject(json, List.class);
            for (Map<String, Object> cmd : list) {
                TaskRespondVO respondVO = this.execute(deviceEntity, cmd);
                if (respondVO != null) {
                    this.consoleService.info("来自云端的请求操作完成：设备名称->" + deviceEntity.getDeviceName() + "cmd->" + JsonUtils.buildJson(cmd));
                } else {
                    this.consoleService.info("来自云端的请求操作失败：设备名称->" + deviceEntity.getDeviceName() + "cmd->" + JsonUtils.buildJson(cmd));
                }

            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private TaskRespondVO execute(DeviceEntity deviceEntity, Map<String, Object> cmd) {
        String id = (String) cmd.get("id");
        String value = (String) cmd.get("value");
        String remark = (String) cmd.get("remark");

        if (MethodUtils.hasEmpty(id, value, remark)) {
            return null;
        }

        // 构造操作请求
        TaskRequestVO requestVO = this.dictService.buildTaskRequestVO(deviceEntity, id, value);
        if (requestVO == null) {
            return null;
        }


        // 向设备服务，发出操作请求
        this.redisClient.getDeviceClient().pushDeviceRequest(requestVO);

        // 等待设备服务响应
        TaskRespondVO respondVO = this.redisClient.getDeviceClient().getDeviceRespond(requestVO.getUuid(), requestVO.getTimeout());

        return respondVO;
    }

    private DeviceEntity getDeviceEntity(String productId, String deviceNum) {
        return this.entityManageService.getEntity(DeviceEntity.class, (Object value) -> {
            DeviceEntity entity = (DeviceEntity) value;

            Object prodId = entity.getDeviceParam().getOrDefault(this.fastBeeService.getProductId(), "");
            Object devNum = entity.getDeviceParam().getOrDefault(this.fastBeeService.getDeviceNum(), "");
            if (MethodUtils.hasEmpty(prodId, devNum)) {
                return false;
            }

            return productId.equals(prodId.toString()) && deviceNum.equals(devNum.toString());
        });
    }


}