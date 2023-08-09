package cn.foxtech.channel.common.linker;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

/**
 * 对完成了Socket Connected步骤的会话，进行下一步的StartLink会话，
 */
@Component
public class LinkerCreateLinkerPeriodTask extends PeriodTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerCreateLinkerPeriodTask.class);

    /**
     * 在建立链路阶段：使用ChannelServerAPI，而不是自带拦截器的 ChannelClientAPI
     */
    @Autowired
    private ChannelServerAPI channelService;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private LinkerMethodTemplate methodTemplate;

    @Override
    public int getTaskType() {
        return PeriodTaskType.task_type_share;
    }

    /**
     * 获得调度周期
     *
     * @return 调度周期，单位秒
     */
    public int getSchedulePeriod() {
        return 1;
    }

    /**
     * 待周期性执行的操作
     */
    public void execute() {
        // 查询：尚未建立连接的channel
        List<LinkerEntity> entities = LinkerManager.queryEntityListByLinkStatus(false);
        if (entities.isEmpty()) {
            return;
        }

        // 创建连接
        for (LinkerEntity entity : entities) {
            this.createLink(entity.getChannelName());
        }
    }

    private void createLink(String channelName) {
        try {
            // 获得ChannelEntity信息
            ChannelEntity channelEntity = this.entityManageService.getChannelEntity(channelName, this.channelProperties.getChannelType());
            if (channelEntity == null) {
                return;
            }

            // 获得解码器信息
            Object linkEncoder = channelEntity.getChannelParam().get(LinkerMethodEntity.PARAM_KEY);
            if (linkEncoder == null) {
                return;
            }

            // 获得编码器
            LinkerMethodEntity methodEntity = this.methodTemplate.getMap().get(linkEncoder);
            if (methodEntity == null) {
                return;
            }


            ChannelRequestVO requestVO = new ChannelRequestVO();
            requestVO.setName(channelName);
            requestVO.setUuid(UUID.randomUUID().toString());
            requestVO.setTimeout(2000);
            requestVO.setSend(methodEntity.getEncodeCreateLinkerRequest().invoke(this, channelEntity.getChannelParam()));

            LOGGER.info("建立链路:" + channelName);
            ChannelRespondVO respondVO = channelService.execute(requestVO);
            if (respondVO.getCode() != 200) {
                LOGGER.info("建立链路失败:" + channelName + ",原因:" + respondVO.getMsg());
                return;
            }

            // 解码设备返回的连接报文，确认是否连接上了
            if (!(boolean) methodEntity.getDecodeCreateLinkerRespond().invoke(this, channelEntity.getChannelParam(), respondVO.getRecv())) {
                LOGGER.info("建立链路失败:" + channelName + ",失败的报文:" + respondVO.getRecv());
                return;
            }

            // 标识为链路已经建立
            LinkerManager.updateEntity4LinkStatus(channelName, true);
            LOGGER.info("建立链路成功:" + channelName);

        } catch (InvocationTargetException ie) {
            LOGGER.info("建立链路失败:" + channelName + ",原因:" + ie.getTargetException());
        } catch (Exception e) {
            LOGGER.info("建立链路失败:" + channelName + ",原因:" + e.getMessage());
        }
    }
}
