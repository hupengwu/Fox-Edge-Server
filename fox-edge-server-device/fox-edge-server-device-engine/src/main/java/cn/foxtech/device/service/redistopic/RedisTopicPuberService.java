package cn.foxtech.device.service.redistopic;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.service.redislist.PersistRecordService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 发送topic
 */
@Component
public class RedisTopicPuberService {
    private static final Logger logger = Logger.getLogger(RedisTopicPuberService.class);

    private static final int TIMEOUT_CHANNEL = 1000;

    /**
     * 发送者
     */
    @Autowired
    private RedisTopicPublisher publisher;

    @Autowired
    private PersistRecordService persistService;

    /**
     * 将请求发送给对应的channel服务：topic_channel_request_XXXX
     *
     * @return 响应报文
     */
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws InterruptedException, TimeoutException, IOException {
        // 填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        if (requestVO.getUuid() == null || requestVO.getUuid().isEmpty()) {
            requestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));
        }

        // 通道类型所属的topic
        String topic = RedisTopicConstant.topic_channel_request + requestVO.getType();

        // 重置信号
        String key = requestVO.getUuid();
        SyncFlagObjectMap.inst().reset(key);

        // 发送数据
        this.publisher.sendMessage(topic, requestVO);

        // 等待消息的到达：根据动态key
        ChannelRespondVO respond = (ChannelRespondVO) SyncFlagObjectMap.inst().waitDynamic(key, requestVO.getTimeout() + TIMEOUT_CHANNEL);
        if (respond == null) {
            throw new TimeoutException("通道服务响应超时：" + requestVO.getType());
        }

        return respond;
    }

    /**
     * 将设备主动上报的报文，发送给topic_device_respond_public
     */
    public void sendReportVO(TaskRespondVO taskRespondVO) {
        // 统一发到public
        String topic = RedisTopicConstant.topic_device_respond + RedisTopicConstant.model_public;

        // 发送数据
        this.publisher.sendMessage(topic, taskRespondVO);
    }

    /**
     * 将设备响应的报文，发送回客户端：topic_device_respond_XXXX
     */
    public void sendRespondVO(TaskRespondVO taskRespondVO) {
        // 如果没填具体的model名称，那么统一发到public那边去
        String model = taskRespondVO.getClientName();
        if (model == null) {
            model = RedisTopicConstant.model_public;
        }
        String topic = RedisTopicConstant.topic_device_respond + model;

        // 发送数据
        this.publisher.sendMessage(topic, taskRespondVO);
    }

    public void sendOperateRespondVO(TaskRespondVO taskRespondVO) {
        try {
            // 筛选：是否有data/value/result属性，如果有的话，这个属性是要独立上报的
            List<OperateRespondVO> respondVOS = new ArrayList<>();
            for (OperateRespondVO operateRespondVO : taskRespondVO.getRespondVOS()) {
                // 场景1：用户手动标识的要求记录的数据
                if (Boolean.TRUE.equals(operateRespondVO.getRecord())) {
                    // 复制一个副本，并重新填入数据
                    OperateRespondVO resultVO = new OperateRespondVO();
                    resultVO.bind(operateRespondVO);
                    resultVO.setRecord(true);

                    // 打入包裹
                    respondVOS.add(resultVO);
                    continue;
                }
                // 场景2：解码器要求强制记录的数据
                Map<String, Object> values = (Map<String, Object>) operateRespondVO.getData().get(DeviceMethodVOFieldConstant.value_data_value);
                if (values != null && values.containsKey(FoxEdgeOperate.result)) {
                    // 复制一个副本，并重新填入数据
                    OperateRespondVO resultVO = new OperateRespondVO();
                    resultVO.bind(operateRespondVO);
                    resultVO.setRecord(true);

                    // 打入包裹
                    respondVOS.add(resultVO);
                    continue;
                }
            }

            // 检查：是否有真正需要记录的操作
            if (respondVOS.isEmpty()) {
                return;
            }

            // 把数据内容，填写为新的操作步骤
            taskRespondVO.setRespondVOS(respondVOS);


            this.persistService.push(taskRespondVO);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
