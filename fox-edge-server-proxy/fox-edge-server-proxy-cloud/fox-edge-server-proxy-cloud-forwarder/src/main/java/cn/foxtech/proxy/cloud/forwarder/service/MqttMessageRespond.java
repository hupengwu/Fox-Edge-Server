package cn.foxtech.proxy.cloud.forwarder.service;


import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.proxy.cloud.forwarder.config.ApplicationConfig;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRequestVO;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRespondVO;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.proxy.cloud.forwarder.service.proxy.HttpRestfulProxyService;
import cn.foxtech.proxy.cloud.forwarder.service.proxy.RedisTopicProxyService;
import net.dreamlu.iot.mqtt.spring.client.MqttClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * MqttMessage的执行者
 */
@Component
public class MqttMessageRespond extends PeriodTaskService {
    @Autowired
    private HttpRestfulProxyService httpRestfulProxyService;

    @Autowired
    private RedisTopicProxyService redisTopicProxyService;

    @Autowired
    private MqttMessageMapping mqttMessageQueue;

    @Autowired
    private MqttClientTemplate mqttClientTemplate;

    @Autowired
    private ApplicationConfig constant;


    @Override
    public void execute(long threadId) throws Exception {
        if (this.mqttMessageQueue.isEmpty()) {
            Thread.sleep(250);
            return;
        }

        // 场景1：发送前面预处理阶段中的拒绝报文
        if (!this.mqttMessageQueue.isEmpty(MqttMessageMapping.TYPE_RESPOND)) {
            List<RestfulLikeRespondVO> respondVOList = this.mqttMessageQueue.removeRespondVOList();

            for (RestfulLikeRespondVO respondVO : respondVOList) {
                // 返回响应消息
                String rspContext = JsonUtils.buildJson(respondVO);

                String pubTopic = this.constant.getPublish();
                this.mqttClientTemplate.publish(pubTopic, rspContext.getBytes(StandardCharsets.UTF_8));
            }
        }

        // 场景2：通过预处理的待执行报文
        if (!this.mqttMessageQueue.isEmpty(MqttMessageMapping.TYPE_REQUEST)) {
            List<RestfulLikeRequestVO> requestVOList = this.mqttMessageQueue.queryRequestVOList();

            for (RestfulLikeRequestVO requestVO : requestVOList) {
                // 记录处理时间
                this.mqttMessageQueue.updateRequestVO(requestVO.getUuid(), System.currentTimeMillis());

                // 执行请求
                RestfulLikeRespondVO respondVO = this.execute(requestVO);

                // 返回响应消息
                String rspContext = JsonUtils.buildJson(respondVO);
                String pubTopic = this.constant.getPublish();
                this.mqttClientTemplate.publish(pubTopic, rspContext.getBytes(StandardCharsets.UTF_8));

                // 删除任务
                this.mqttMessageQueue.deleteRequestVO(requestVO.getUuid());
            }

            // 删除超时响应的数据
            this.mqttMessageQueue.deleteRequestVO(60 * 1000);
        }

    }

    /**
     * 执行请求
     *
     * @param requestVO
     * @return
     */
    private RestfulLikeRespondVO execute(RestfulLikeRequestVO requestVO) {
        try {
            // 执行Restful API服务接口的请求
            if (this.httpRestfulProxyService.isHttpResource(requestVO.getResource())) {
                // 执行HTTP请求
                RestfulLikeRespondVO respondVO = this.httpRestfulProxyService.execute(requestVO);
                if (respondVO == null) {
                    respondVO = RestfulLikeRespondVO.error("操作失败!");
                }

                respondVO.setResource(requestVO.getResource());
                respondVO.setMethod(requestVO.getMethod());
                respondVO.setUuid(requestVO.getUuid());
                return respondVO;
            }

            // 执行RedisTopic API服务接口的请求
            if (this.redisTopicProxyService.isRedisResource(requestVO.getResource())) {
                // 执行HTTP请求
                RestfulLikeRespondVO respondVO = this.redisTopicProxyService.execute(requestVO);
                if (respondVO == null) {
                    respondVO = RestfulLikeRespondVO.error("操作失败!");
                }

                respondVO.setResource(requestVO.getResource());
                respondVO.setMethod(requestVO.getMethod());
                respondVO.setUuid(requestVO.getUuid());
                return respondVO;
            }

            throw new ServiceException("尚未支持的方法");
        } catch (Exception e) {
            RestfulLikeRespondVO respondVO = RestfulLikeRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindVO(requestVO);
            }

            return respondVO;
        }
    }
}
