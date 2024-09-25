/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */

package cn.foxtech.rpc.sdk.demo.test;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.RestfulLikeRequestVO;
import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.rpc.sdk.mqtt.RpcSdkMqttClient;
import cn.foxtech.rpc.sdk.mqtt.RpcSdkMqttHelper;
import cn.foxtech.rpc.sdk.mqtt.remote.RemoteMqttHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RpcMqttSdkTester {
    private static final Logger logger = Logger.getLogger(RpcMqttSdkTester.class);
    private final String edgeId = "F9509B1CE7B0F023";
    @Autowired
    private RpcSdkMqttClient mqttClient;
    @Autowired
    private RpcSdkMqttHelper mqttHelper;

    public void test() {
        this.initialize();
        this.testManager();
        this.testDeviceTask1();
        this.testDeviceTask2();
        this.testChannelTask();
    }

    private void initialize() {
        // 生成一个缺省的mqtt配置参数，你也可以自己构造一个相同格式的MQTT参数
        Map<String, Object> mqttConfig = this.mqttClient.buildMqttDefaultConfig();
        mqttConfig.put("host", "39.108.137.38");// 这是默认的配置，建议自行管理mqttConfig


        // 生成一个接收MQTT消息的handler，指明要订阅的消息
        RemoteMqttHandler mqttHandler = new RemoteMqttHandler();// 这是默认的handler实现，建议根据需要自行实现一个mqttHandler
        mqttHandler.setTopic("/fox/manager/e2c/forward/" + this.edgeId);

        this.mqttClient.setMqttConfig(mqttConfig);
        this.mqttClient.initialize(mqttHandler);

        // 等待连接建立
        this.mqttClient.waitConnected(60 * 1000);
    }

    private void testManager() {
        RestfulLikeRequestVO requestVO = new RestfulLikeRequestVO();
        requestVO.setTopic("/fox/manager/c2e/F9509B1CE7B0F023/forward");// 必填参数：跟fox-edge的manager约定的topic
        requestVO.setUuid(UUID.randomUUID().toString());// 必填参数：待会用来查询响应的报文消息

        // 参考管理页面的浏览器查询的restful接口
        Map<String, Object> body = new HashMap<>();
        body.put("pageNum", 1);
        body.put("pageSize", 10);
        requestVO.setResource("/device/page");
        requestVO.setMethod("post");
        requestVO.setBody(body);

        // 发送请求
        this.mqttClient.sendRequest(requestVO);
        logger.info("管理服务-发送MQTT请求：" + JsonUtils.buildJsonWithoutException(requestVO));

        // 等待响应：MQTT消息订阅的RemoteMqttHandler，会通知你是否收到Fox-Edge发送给你的消息
        RestfulLikeRespondVO respondVO = this.mqttClient.waitRespond(requestVO.getUuid(), 10 * 1000);
        logger.info("管理服务-返回MQTT响应：" + JsonUtils.buildJsonWithoutException(respondVO));
    }

    private void testDeviceTask1() {
        RestfulLikeRequestVO requestVO = new RestfulLikeRequestVO();
        requestVO.setTopic("/fox/manager/c2e/" + this.edgeId + "/forward");// 必填参数：跟fox-edge的manager约定的topic
        requestVO.setUuid(UUID.randomUUID().toString());// 必填参数：待会用来查询响应的报文消息


        // 根据模板的参数，开始构造发送给设备的批量服务请求
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.setClientName("my name is tester");
        taskRequestVO.setUuid(UUID.randomUUID().toString());

        Map<String, Object> deviceParam = new HashMap<>();
        Map<String, Object> operateParam = new HashMap<>();

        // 操作命令1
        OperateRequestVO operateRequestVO1 = new OperateRequestVO();
        operateRequestVO1.setManufacturer("深圳安圣电气有限公司");// 设备厂商
        operateRequestVO1.setDeviceType("CE+T UPS");// 设备类型
        operateRequestVO1.setDeviceName("CE+T UPS设备-718");//设备名称
        operateRequestVO1.setUuid(UUID.randomUUID().toString().replace("-", ""));// UUID
        operateRequestVO1.setOperateMode(DeviceMethodVOFieldConstant.value_operate_exchange);// 操作方式
        operateRequestVO1.setOperateName("Read System Measures Table");// 操作方法
        operateRequestVO1.getParam().putAll(operateParam);// 操作参数
        operateRequestVO1.getParam().putAll(deviceParam);// 设备参数
        operateRequestVO1.setTimeout(2000);// 通讯超时

        OperateRequestVO operateRequestVO2 = new OperateRequestVO();
        operateRequestVO2.setManufacturer("深圳安圣电气有限公司");// 设备厂商
        operateRequestVO2.setDeviceType("CE+T UPS");// 设备类型
        operateRequestVO2.setDeviceName("CE+T UPS设备-718");//设备名称
        operateRequestVO2.setUuid(UUID.randomUUID().toString().replace("-", ""));// UUID
        operateRequestVO2.setOperateMode(DeviceMethodVOFieldConstant.value_operate_exchange);// 操作方式
        operateRequestVO2.setOperateName("Read Alarms And Events Table");// 操作方法
        operateRequestVO2.getParam().putAll(operateParam);// 操作参数
        operateRequestVO2.getParam().putAll(deviceParam);// 设备参数
        operateRequestVO2.setTimeout(2000);// 通讯超时

        taskRequestVO.getRequestVOS().add(operateRequestVO1);
        taskRequestVO.getRequestVOS().add(operateRequestVO2);
        taskRequestVO.setTimeout(operateRequestVO1.getTimeout() + operateRequestVO2.getTimeout());


        requestVO.setResource("/proxy-redis-topic/proxy/redis/topic/device");
        requestVO.setMethod("post");
        requestVO.setBody(taskRequestVO);


        this.mqttClient.sendRequest(requestVO);
        logger.info("设备服务-发送请求：" + JsonUtils.buildJsonWithoutException(taskRequestVO));

        // 等待响应：MQTT消息订阅的RemoteMqttHandler，会通知你是否收到Fox-Edge发送给你的消息
        RestfulLikeRespondVO respondVO = this.mqttClient.waitRespond(requestVO.getUuid(), 10 * 1000);
        logger.info("管理服务-返回MQTT响应：" + JsonUtils.buildJsonWithoutException(respondVO));
    }

    private void testDeviceTask2() {
        // 步骤1：设备参数：可以通过DeviceEntity转换为Map的方式，得到它
        Map<String, Object> deviceEntity = new HashMap<>();
        deviceEntity.put("manufacturer", "深圳安圣电气有限公司");
        deviceEntity.put("deviceType", "CE+T UPS");
        deviceEntity.put("deviceName", "CE+T UPS设备-718");
        Map<String, Object> deviceParam = new HashMap<>();
        deviceParam.put("设备地址", 1);
        deviceEntity.put("deviceParam", deviceParam);

        // 步骤2：操作任务参数，可以参考前端界面填写的参数
        String operateMode = DeviceMethodVOFieldConstant.value_operate_exchange;
        String operateName = "Read Alarms And Events Table";
        Map<String, Object> taskParam = new HashMap<>();
        Integer timeout = 2000;

        // 步骤3：构造成TaskRequestVO对象
        TaskRequestVO taskRequestVO = this.mqttHelper.buildTaskRequestVO(deviceEntity, deviceParam, operateMode, operateName, taskParam, timeout);
        logger.info("设备服务-发送请求：" + JsonUtils.buildJsonWithoutException(taskRequestVO));

        // 步骤4：对远端的Fox-Edge进行操作
        TaskRespondVO taskRespondVO = this.mqttHelper.executeDeviceOperateTask("F9509B1CE7B0F023", taskRequestVO);
        logger.info("管理服务-返回MQTT响应：" + JsonUtils.buildJsonWithoutException(taskRespondVO));
    }

    private void testChannelTask() {
        ChannelRequestVO channelRequestVO = new ChannelRequestVO();
        channelRequestVO.setUuid(UUID.randomUUID().toString());
        channelRequestVO.setType("simulator");
        channelRequestVO.setName("channel-simulator");
        channelRequestVO.setMode(ChannelRequestVO.MODE_EXCHANGE);
        channelRequestVO.setSend("01 03 00 00 00 01 84 0A");
        channelRequestVO.setTimeout(2000);
        logger.info("设备服务-发送请求：" + JsonUtils.buildJsonWithoutException(channelRequestVO));

        ChannelRespondVO channelRespondVO = this.mqttHelper.executeChannelOperateTask("F9509B1CE7B0F023", channelRequestVO);
        logger.info("管理服务-返回MQTT响应：" + JsonUtils.buildJsonWithoutException(channelRespondVO));
    }
}
