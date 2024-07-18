package cn.foxtech.kernel.system.service.restfullike.redis;


import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.rpc.redis.manager.server.RedisListManagerServer;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Topic的订阅<br>
 * 背景：某些服务会通过redis topic主动发送请求
 */
@Component
public class RedisRestfulLikeScheduler extends PeriodTaskService {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RedisRestfulLikeController controller;


    @Autowired
    private RedisListManagerServer managerServer;

    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        RestFulRequestVO requestVO = this.managerServer.popRequest(1, TimeUnit.SECONDS);
        if (requestVO == null) {
            return;
        }

        // 执行
        RestFulRespondVO respondVO = this.execute(requestVO);

        // 检查是否需要返回数据：如果有UUID，说明客户端会根据UUID取数据
        if (!MethodUtils.hasEmpty(requestVO.getUuid())) {
            this.managerServer.pushRespond(requestVO.getUuid(), respondVO);
        }
    }

    private RestFulRespondVO execute(RestFulRequestVO requestVO) {
        try {
            if (MethodUtils.hasEmpty(requestVO.getUuid(), requestVO.getUri(), requestVO.getMethod())) {
                throw new ServiceException("参数缺失：uuid, resource, method");
            }

            Object value = this.controller.execute(requestVO.getUri(), requestVO.getMethod().toUpperCase(), requestVO.getData());

            RestFulRespondVO respondVO = new RestFulRespondVO();
            respondVO.bindResVO(requestVO);
            respondVO.setData(value);

            // 执行请求
            return respondVO;

        } catch (Exception e) {
            RestFulRespondVO respondVO = RestFulRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindResVO(requestVO);
            }

            return respondVO;
        }
    }

}
