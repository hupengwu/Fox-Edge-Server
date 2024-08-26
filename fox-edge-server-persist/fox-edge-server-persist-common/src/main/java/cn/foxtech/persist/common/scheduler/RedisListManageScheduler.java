/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.common.scheduler;

import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.rpc.redis.persist.server.RedisListPersistServer;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.persist.common.service.PersistManageService;
import cn.foxtech.persist.common.service.EntityUpdateService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 记录队列：从topic改为采用list方式，是为了让记录数据更可靠
 */
@Component
public class RedisListManageScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(RedisListManageScheduler.class);
    @Autowired
    PersistManageService entityManageService;
    @Autowired
    EntityUpdateService entityUpdateService;

    /**
     * 设备管理：请求
     */
    @Autowired
    private RedisListPersistServer persistServer;

    /**
     * 前台日志
     */
    @Autowired
    private RedisConsoleService console;

    @Override
    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        RestFulRequestVO respondMap = this.persistServer.popManageRequest(1, TimeUnit.SECONDS);
        if (respondMap == null) {
            return;
        }

        // 处理数据
        this.updateManageRequest(respondMap);
    }


    private void updateManageRequest(RestFulRequestVO requestVO) {
        try {
            // 场景1： 删除设备数值
            if (RestFulManagerVOConstant.uri_device_value.equals(requestVO.getUri()) && "delete".equals(requestVO.getMethod())) {
                RestFulRespondVO respondVO = this.entityUpdateService.deleteValueEntity(requestVO);
                this.persistServer.pushManageRequest(respondVO);
                return;
            }

        } catch (Exception e) {
            String message = "更新设备数据，发生异常：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }

}