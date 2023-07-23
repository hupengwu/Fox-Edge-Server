package cn.foxtech.manager.system.scheduler;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import cn.foxtech.common.domain.vo.PublicRespondVO;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.redistopic.IRedisTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 对Channel发送过来的消息进行响应处理
 */
@Component
public class TopicRespondScheduler implements IRedisTopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicRespondScheduler.class);

    @Override
    public void respondDevice(PublicRespondVO respondVO) throws ServiceException {

    }

    @Override
    public void respondChannel(ChannelRespondVO respondVO) throws ServiceException {
        try {
            if (respondVO.getUuid() != null && !respondVO.getUuid().isEmpty()) {
                SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), respondVO);
            }
        } catch (Exception e) {
            logger.warn(e.toString());
        }
    }

    @Override
    public void respondPersist(RestFulRespondVO respondVO) throws ServiceException {
        try {
            if (respondVO.getUuid() != null && !respondVO.getUuid().isEmpty()) {
                SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), respondVO);
            }
        } catch (Exception e) {
            logger.warn(e.toString());
        }
    }

    @Override
    public void requestManager(RestFulRequestVO requestVO) throws ServiceException {
        try {
            if (requestVO.getUuid() != null && !requestVO.getUuid().isEmpty()) {
                SyncQueueObjectMap.inst().push(RestFulManagerVOConstant.restful_manager, requestVO, 16);
            }
        } catch (Exception e) {
            logger.warn(e.toString());
        }
    }
}
