package cn.foxtech.rpc.sdk.mqtt;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.RestfulLikeRequestVO;
import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * 对设备的通道级和设备级的远程操作，进行一层再封装，简化开发人员的代码工作量
 */
@Component
public class RpcSdkMqttHelper {
    @Autowired
    private RpcSdkMqttClient mqttClient;

    public TaskRequestVO buildTaskRequestVO(Map<String, Object> deviceEntity, String operateMode, String operateName, Map<String, Object> taskParam) {
        Map<String, Object> deviceParam = (Map<String, Object>) deviceEntity.get("deviceParam");
        return this.buildTaskRequestVO(deviceEntity, deviceParam, operateMode, operateName, taskParam, 2000, false);
    }

    public TaskRequestVO buildTaskRequestVO(Map<String, Object> deviceEntity, Map<String, Object> deviceParam, String operateMode, String operateName, Map<String, Object> taskParam) {
        return this.buildTaskRequestVO(deviceEntity, deviceParam, operateMode, operateName, taskParam, 2000);
    }

    public TaskRequestVO buildTaskRequestVO(Map<String, Object> deviceEntity, Map<String, Object> deviceParam, String operateMode, String operateName, Map<String, Object> taskParam, Integer timeout) {
        return this.buildTaskRequestVO(deviceEntity, deviceParam, operateMode, operateName, taskParam, timeout, false);
    }

    /**
     * 构造单个命令的设备操作任务
     *
     * @param deviceEntity
     * @param deviceParam
     * @param operateMode
     * @param operateName
     * @param taskParam
     * @param timeout
     * @param record
     * @return
     */
    public TaskRequestVO buildTaskRequestVO(Map<String, Object> deviceEntity, Map<String, Object> deviceParam, String operateMode, String operateName, Map<String, Object> taskParam, Integer timeout, Boolean record) {
        String manufacturer = (String) deviceEntity.get("manufacturer");
        String deviceType = (String) deviceEntity.get("deviceType");
        String deviceName = (String) deviceEntity.get("deviceName");

        // 检查：参数是否为空
        if (MethodUtils.hasEmpty(manufacturer, deviceType, deviceName)) {
            throw new ServiceException("输入参数参数：deviceEntity中，manufacturer, deviceType, deviceName不能为空，它是由DeviceEntity转换的Map，不应该为空!");
        }
        if (MethodUtils.hasNull(deviceParam)) {
            throw new ServiceException("输入参数参数：deviceParam不能为空，它是由DeviceEntity转换的Map，不应该为空!");
        }
        if (MethodUtils.hasEmpty(operateMode, operateName, timeout)) {
            throw new ServiceException("输入参数：operateMode, operateName, timeout不能为空！");
        }
        if (MethodUtils.hasNull(taskParam)) {
            throw new ServiceException("输入参数参数：taskParam不能为空！");
        }
        if (record == null) {
            record = true;
        }

        // 根据模板的参数，开始构造发送给设备的批量服务请求
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));

        Integer totalTimeout = 0;

        // 单个设备的操作
        OperateRequestVO operateRequestVO = new OperateRequestVO();
        operateRequestVO.setManufacturer(manufacturer);
        operateRequestVO.setDeviceType(deviceType);
        operateRequestVO.setDeviceName(deviceName);
        operateRequestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));
        operateRequestVO.setRecord(record);
        operateRequestVO.setOperateMode(operateMode);
        operateRequestVO.setOperateName(operateName);
        operateRequestVO.setTimeout(timeout);

        // 合并来自设备参数：也就是如果有重复字段，会设备参数会优先覆盖模板参数
        if (taskParam != null) {
            operateRequestVO.getParam().putAll(taskParam);
        }
        if (deviceParam != null) {
            operateRequestVO.getParam().putAll(deviceParam);
        }

        taskRequestVO.getRequestVOS().add(operateRequestVO);

        totalTimeout += operateRequestVO.getTimeout();

        taskRequestVO.setTimeout(totalTimeout);

        return taskRequestVO;
    }

    /**
     * 执行设备操作
     *
     * @param edgeId Fox-Edge的ID
     * @param taskRequestVO 设备操作任务
     * @return
     */
    public TaskRespondVO executeDeviceOperateTask(String edgeId, TaskRequestVO taskRequestVO) {
        try {
            RestfulLikeRequestVO restfulLikeRequestVO = new RestfulLikeRequestVO();
            restfulLikeRequestVO.setTopic("/fox/manager/c2e/" + edgeId + "/forward");// 必填参数：跟fox-edge的manager约定的topic
            restfulLikeRequestVO.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));// 必填参数：待会用来查询响应的报文消息

            // 参考管理页面的浏览器查询的restful接口
            Map<String, Object> body = JsonUtils.buildObject(taskRequestVO, Map.class);
            restfulLikeRequestVO.setResource("/proxy-redis-topic/proxy/redis/topic/device");
            restfulLikeRequestVO.setMethod("post");
            restfulLikeRequestVO.setBody(body);

            // 发送请求
            this.mqttClient.sendRequest(restfulLikeRequestVO);

            // 等待响应：MQTT消息订阅的RemoteMqttHandler，会通知你是否收到Fox-Edge发送给你的消息
            RestfulLikeRespondVO restfulLikeRespondVO = this.mqttClient.waitRespond(restfulLikeRequestVO.getUuid(), taskRequestVO.getTimeout() + 10 * 1000);
            if (restfulLikeRespondVO == null) {
                throw new ServiceException("Fox-Edge返回respondVO为空!");
            }

            if (restfulLikeRespondVO.getBody() == null) {
                throw new ServiceException("Fox-Edge返回Body为空!");
            }

            return TaskRespondVO.buildRespondVO((Map<String, Object>) restfulLikeRespondVO.getBody());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 执行通道级的操作
     *
     * @param edgeId Fox-Edge的ID
     * @param channelRequestVO 通道操作任务
     * @return
     */
    public ChannelRespondVO executeChannelOperateTask(String edgeId, ChannelRequestVO channelRequestVO) {
        try {
            RestfulLikeRequestVO requestVO = new RestfulLikeRequestVO();
            requestVO.setTopic("/fox/manager/c2e/" + edgeId + "/forward");// 必填参数：跟fox-edge的manager约定的topic
            requestVO.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));// 必填参数：待会用来查询响应的报文消息

            // 参考管理页面的浏览器查询的restful接口
            Map<String, Object> body = JsonUtils.buildObject(channelRequestVO, Map.class);
            requestVO.setResource("/proxy-redis-topic/proxy/redis/topic/channel");
            requestVO.setMethod("post");
            requestVO.setBody(body);

            // 发送请求
            this.mqttClient.sendRequest(requestVO);

            // 等待响应：MQTT消息订阅的RemoteMqttHandler，会通知你是否收到Fox-Edge发送给你的消息
            RestfulLikeRespondVO respondVO = this.mqttClient.waitRespond(requestVO.getUuid(), channelRequestVO.getTimeout() + 10 * 1000);
            if (respondVO == null) {
                throw new ServiceException("Fox-Edge返回respondVO为空!");
            }

            if (respondVO.getBody() == null) {
                throw new ServiceException("Fox-Edge返回Body为空!");
            }

            return ChannelRespondVO.buildVO((Map<String, Object>) respondVO.getBody());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
