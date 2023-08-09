package cn.foxtech.channel.common.linker;

import cn.foxtech.channel.common.api.ChannelClientAPI;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.utils.method.MethodUtils;
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
 * 对完成了进行下一步的StartLink的会话，进行下一步的TestLink会话，
 */
@Component
public class LinkerActiveLinkerPeriodTask extends PeriodTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerActiveLinkerPeriodTask.class);

    @Autowired
    private ChannelClientAPI channelService;

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
        return 5;
    }

    /**
     * 待周期性执行的操作
     */
    public void execute() {
        // 查询：已经完成了启动链路的Channel
        List<LinkerEntity> entities = LinkerManager.queryEntityListByLinkStatus(true);
        if (entities.isEmpty()) {
            return;
        }

        // 创建连接
        for (LinkerEntity entity : entities) {
            this.activeLink(entity.getChannelName());
        }
    }

    private void activeLink(String channelName) {
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

            // 构造心跳报文
            Object send = methodEntity.getEncodeActiveLinkerRequest().invoke(this, channelEntity.getChannelParam());
            if (MethodUtils.hasEmpty(send)) {
                return;
            }

            ChannelRequestVO requestVO = new ChannelRequestVO();
            requestVO.setName(channelName);
            requestVO.setUuid(UUID.randomUUID().toString());
            requestVO.setTimeout(2000);
            requestVO.setSend(send);

            ChannelRespondVO respondVO = channelService.execute(requestVO);
            if (respondVO.getCode() != 200) {
                LOGGER.info("激活链路失败:" + channelName);

                LinkerManager.updateEntity4ActiveLinker(channelName, false);
                return;
            }

            // 解码设备返回的心跳报文，确认是否有响应
            if (!(boolean) methodEntity.getDecodeActiveLinkerRespond().invoke(this, channelEntity.getChannelParam(), respondVO.getRecv())) {
                LOGGER.info("激活链路失败:" + channelName);

                LinkerManager.updateEntity4ActiveLinker(channelName, false);
                return;
            }

            // 更新心跳信息
            LinkerManager.updateEntity4ActiveLinker(channelName, true);
        } catch (InvocationTargetException ie) {
            LOGGER.info("激活链路失败:" + channelName + ",原因:" + ie.getTargetException());
        } catch (Exception e) {
            LOGGER.info("激活链路失败:" + channelName + ",原因:" + e.getMessage());
        }
    }
}
