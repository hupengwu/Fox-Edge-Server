package cn.foxtech.kernel.system.common.scheduler;


import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.redistopic.IRedisTopicController;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Topic的订阅<br>
 * 背景：某些服务会通过redis topic主动发送请求
 */
@Component
public class TopicManagerScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(TopicManagerScheduler.class);

    private final String pubTopic = RedisTopicConstant.topic_manager_respond + RedisTopicConstant.model_public;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private IRedisTopicController controller;

    @Autowired
    private RedisTopicPublisher publisher;

    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        // 管理服务发过来的管理请求
        List<Object> requestVOList = SyncQueueObjectMap.inst().popup(RestFulManagerVOConstant.restful_manager, false, 16);
        for (Object request : requestVOList) {
            RestFulRequestVO requestVO = (RestFulRequestVO) request;

            // 执行
            RestFulRespondVO respondVO = this.execute(requestVO);

            // 返回数据
            this.publisher.sendMessage(this.pubTopic, respondVO);
        }
    }

    private RestFulRespondVO execute(RestFulRequestVO requestVO) {
        try {
            if (MethodUtils.hasEmpty(requestVO.getUuid(), requestVO.getUri(), requestVO.getMethod())) {
                throw new ServiceException("参数缺失：uuid, resource, method");
            }


            // 执行请求
            return this.controller.execute(requestVO);

        } catch (Exception e) {
            RestFulRespondVO respondVO = RestFulRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindResVO(requestVO);
            }

            return respondVO;
        }
    }

}
