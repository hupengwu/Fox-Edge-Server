package cn.foxtech.device.service.controller;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.rpc.redis.channel.client.RedisListChannelClient;
import cn.foxtech.common.rpc.redis.device.server.RedisListDeviceServer;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.device.service.service.EntityManageService;
import cn.foxtech.device.service.service.OperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class DeviceReportController extends PeriodTaskService {
    @Autowired
    private EntityManageService entityService;
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;


    @Autowired
    private RedisListDeviceServer deviceServer;

    @Autowired
    private RedisListChannelClient channelClient;

    @Autowired
    private OperateService operateService;


    /**
     * 执行任务
     *
     * @param threadId 线程ID
     * @throws Exception 异常信息
     */
    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        ChannelRespondVO channelRespondVO = this.channelClient.popChannelReport(1, TimeUnit.SECONDS);
        if (channelRespondVO == null) {
            return;
        }

        // 对数据进行解码处理
        this.decodeEvent(channelRespondVO);
    }


    /**
     * 单步操作
     */
    private void decodeEvent(ChannelRespondVO channelRespondVO) {
        String channelType = channelRespondVO.getType();
        String channelName = channelRespondVO.getName();
        Object recv = channelRespondVO.getRecv();

        List<BaseEntity> entityList = this.entityService.getEntityList(DeviceEntity.class);
        for (BaseEntity baseEntity : entityList) {
            DeviceEntity entity = (DeviceEntity) baseEntity;
            if (!entity.getChannelName().equals(channelName)) {
                continue;
            }
            if (!entity.getChannelType().equals(channelType)) {
                continue;
            }

            try {
                Map<String, Object> param = entity.getDeviceParam();
                if (param == null) {
                    param = new HashMap<>();
                }

                // 尝试解码操作：解码不成功的，抛出异常
                OperateRespondVO operateRespondVO = this.operateService.decodeReport(entity, recv, param);
                operateRespondVO.setOperateMode(DeviceMethodVOFieldConstant.value_operate_report);

                // 打包成为单操作的包格式
                TaskRespondVO taskRespondVO = TaskRespondVO.buildRespondVO(operateRespondVO, null);

                // 上报数据到public
                this.deviceServer.pushDeviceReport(taskRespondVO);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }
    }

}
