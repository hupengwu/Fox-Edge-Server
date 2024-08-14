/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.common.scheduler;

import cn.foxtech.channel.common.api.ChannelClientAPI;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.rpc.redis.channel.server.RedisListChannelServer;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 记录队列：从topic改为采用list方式，是为了让记录数据更可靠
 */
@Component
public class RedisListRespondScheduler extends PeriodTaskService {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    /**
     * 设备记录
     */
    @Autowired
    private RedisListChannelServer channelServer;

    @Autowired
    private ChannelClientAPI channelService;


    @Autowired
    private ChannelProperties constants;


    @Override
    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }


        ChannelRequestVO requestVO = this.channelServer.popChannelRequest(1, TimeUnit.SECONDS);
        if (requestVO == null) {
            return;
        }

        // 处理数据
        this.updateDeviceRespond(requestVO);
    }

    private void updateDeviceRespond(ChannelRequestVO requestVO) {
        try {
            if (ChannelRequestVO.MODE_EXCHANGE.equals(requestVO.getMode())) {
                // 一问一答模式
                ChannelRespondVO respondVO = this.execute(requestVO);

                // 将UUID回填回去
                respondVO.setUuid(requestVO.getUuid());
                respondVO.setType(constants.getChannelType());

                // 返回数据
                this.channelServer.pushChannelRespond(respondVO.getUuid(), respondVO);

            } else if (ChannelRequestVO.MODE_PUBLISH.equals(requestVO.getMode())) {
                // 单向发布模式
                ChannelRespondVO respondVO = this.publish(requestVO);

                // 将UUID回填回去
                respondVO.setUuid(requestVO.getUuid());
                respondVO.setType(constants.getChannelType());

                // 返回数据
                this.channelServer.pushChannelRespond(respondVO.getUuid(), respondVO);
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