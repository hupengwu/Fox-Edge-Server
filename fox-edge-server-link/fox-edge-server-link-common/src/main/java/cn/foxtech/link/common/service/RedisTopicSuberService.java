package cn.foxtech.link.common.service;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.link.common.api.LinkClientAPI;
import cn.foxtech.link.common.properties.LinkProperties;
import cn.foxtech.link.domain.LinkRequestVO;
import cn.foxtech.link.domain.LinkRespondVO;
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
    private LinkClientAPI linkService;

    @Autowired
    private LinkProperties linkProperties;


    @Override
    public String topic1st() {

        return RedisTopicConstant.topic_link_request + linkProperties.getLinkType();
    }

    @Override
    public void receiveTopic1st(String message) {
        try {
            LinkRequestVO requestVO = JsonUtils.buildObject(message, LinkRequestVO.class);
            LinkRespondVO respondVO;

            if (LinkRequestVO.MODE_MANAGE.equals(requestVO.getMode())) {
                // 管理模式
                respondVO = this.manage(requestVO);

                // 将UUID回填回去
                respondVO.setUuid(requestVO.getUuid());
                respondVO.setType(linkProperties.getLinkType());
                String json = JsonUtils.buildJson(respondVO);

                // 填充到缓存队列
                SyncQueueObjectMap.inst().push(RedisTopicConstant.topic_link_respond + RedisTopicConstant.model_manager, json, 10);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private LinkRespondVO manage(LinkRequestVO requestVO) {
        try {
            return this.linkService.manageLink(requestVO);
        } catch (Exception e) {
            return LinkRespondVO.error(requestVO, "publish 操作失败：" + e);
        }
    }
}
