package cn.foxtech.channel.common.service;

import cn.foxtech.channel.common.api.ChannelClientAPI;
import cn.foxtech.channel.common.constant.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.constant.VOFieldConstant;
import cn.foxtech.common.domain.vo.PublicRequestVO;
import cn.foxtech.common.domain.vo.PublicRespondVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    public String topic2nd() {
        return RedisTopicConstant.topic_channel_request + RedisTopicConstant.model_manager;
    }

    @Override
    public void receiveTopic1st(String message) {
        try {
            ChannelRequestVO requestVO = JsonUtils.buildObject(message, ChannelRequestVO.class);
            ChannelRespondVO respondVO = null;

            if (ChannelRequestVO.MODE_EXCHANGE.equals(requestVO.getMode())) {
                // 一问一答模式
                respondVO = this.execute(requestVO);
            } else if (ChannelRequestVO.MODE_PUBLISH.equals(requestVO.getMode())) {
                // 单向发布模式
                respondVO = this.publish(requestVO);
            } else {
                return;
            }


            // 将UUID回填回去
            respondVO.setUuid(requestVO.getUuid());
            respondVO.setType(constants.getChannelType());
            String json = JsonUtils.buildJson(respondVO);

            // 填充到缓存队列
            SyncQueueObjectMap.inst().push(RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_device, json, 1000);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public void receiveTopic2nd(String message) {
        try {
            Map<String, Object> map = JsonUtils.buildObject(message, Map.class);

            Object cmd = map.get(VOFieldConstant.field_cmd);

            PublicRequestVO requestVO = (PublicRequestVO) BeanMapUtils.mapToObject(map, PublicRequestVO.class);
            PublicRespondVO respondVO = null;
            // 查询通道名称列表
            if (VOFieldConstant.value_cmd_query_name.equals(cmd)) {
                respondVO = channelService.getChannelNameList(requestVO);
            }

            // 将UUID回填回去
            respondVO.setModelType(RedisTopicConstant.model_channel);
            respondVO.setModelName(constants.getChannelType());
            String json = JsonUtils.buildJson(respondVO);

            // 填充到缓存队列
            SyncQueueObjectMap.inst().push(RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_manager, json, 1000);

        } catch (Exception e) {
            logger.warn(e.getMessage());
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

            ChannelRespondVO respondVO = channelService.execute(requestVO);
            return respondVO;
        } catch (Exception e) {
            return ChannelRespondVO.error(requestVO, "exchange 操作失败：" + e.getMessage());
        }
    }

    private ChannelRespondVO publish(ChannelRequestVO requestVO) {
        try {
            channelService.publish(requestVO);

            // 返回数据
            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(null);
            return respondVO;
        } catch (Exception e) {
            return ChannelRespondVO.error(requestVO, "publish 操作失败：" + e);
        }
    }
}
