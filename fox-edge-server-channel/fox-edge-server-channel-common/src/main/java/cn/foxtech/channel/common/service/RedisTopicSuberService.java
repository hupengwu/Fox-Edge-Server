package cn.foxtech.channel.common.service;

import cn.foxtech.channel.common.api.ChannelClientAPI;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicSuberService extends RedisTopicSubscriber {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private ChannelClientAPI channelService;

    @Autowired
    private ChannelProperties constants;


    @Override
    public String topic1st() {

        return RedisTopicConstant.topic_channel_request + constants.getChannelType();
    }

    @Override
    public void receiveTopic1st(String message) {
        try {
            ChannelRequestVO requestVO = JsonUtils.buildObject(message, ChannelRequestVO.class);
            ChannelRespondVO respondVO;

            if (ChannelRequestVO.MODE_EXCHANGE.equals(requestVO.getMode())) {
                // 一问一答模式
                respondVO = this.execute(requestVO);

                // 检查：是否发送到指定路由，如果没有说明，则默认发到设备接收的topic
                if (MethodUtils.hasEmpty(respondVO.getRoute())){
                    respondVO.setRoute(RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_device);
                }

                // 将UUID回填回去
                respondVO.setUuid(requestVO.getUuid());
                respondVO.setType(constants.getChannelType());
                String json = JsonUtils.buildJson(respondVO);

                // 填充到缓存队列
                SyncQueueObjectMap.inst().push(respondVO.getRoute(), json, 1000);

            } else if (ChannelRequestVO.MODE_PUBLISH.equals(requestVO.getMode())) {
                // 单向发布模式
                respondVO = this.publish(requestVO);

                // 检查：是否发送到指定路由，如果没有说明，则默认发到设备接收的topic
                if (MethodUtils.hasEmpty(respondVO.getRoute())){
                    respondVO.setRoute(RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_device);
                }

                // 将UUID回填回去
                respondVO.setUuid(requestVO.getUuid());
                respondVO.setType(constants.getChannelType());
                String json = JsonUtils.buildJson(respondVO);

                // 填充到缓存队列
                SyncQueueObjectMap.inst().push(respondVO.getRoute(), json, 1000);
            } else if (ChannelRequestVO.MODE_MANAGE.equals(requestVO.getMode())) {
                // 管理模式
                respondVO = this.manage(requestVO);

                // 将UUID回填回去
                respondVO.setUuid(requestVO.getUuid());
                respondVO.setType(constants.getChannelType());
                String json = JsonUtils.buildJson(respondVO);

                // 填充到缓存队列
                SyncQueueObjectMap.inst().push(RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_manager, json, 10);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 执行主从半双工问答
     *
     * @param requestVO 请求
     * @return 返回
     */
    private ChannelRespondVO execute(ChannelRequestVO requestVO) {
        try {
            if (requestVO.getTimeout() > 60 * 1000) {
                throw new ServiceException("为了避免设备没响应时造成堵塞，不允许最大超时大于1分钟!");
            }

            return this.channelService.execute(requestVO);
        } catch (Exception e) {
            return ChannelRespondVO.error(requestVO, "exchange 操作失败：" + e.getMessage());
        }
    }

    private ChannelRespondVO publish(ChannelRequestVO requestVO) {
        try {
            this.channelService.publish(requestVO);

            // 返回数据
            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(null);
            return respondVO;
        } catch (Exception e) {
            return ChannelRespondVO.error(requestVO, "publish 操作失败：" + e.getMessage());
        }
    }

    private ChannelRespondVO manage(ChannelRequestVO requestVO) {
        try {
            return this.channelService.manageChannel(requestVO);
        } catch (Exception e) {
            return ChannelRespondVO.error(requestVO, "publish 操作失败：" + e.getMessage());
        }
    }
}
