package cn.foxtech.device.service.controller;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.device.service.redistopic.RedisTopicPuberService;
import cn.foxtech.device.service.service.EntityManageService;
import cn.foxtech.device.service.service.OperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RedisTopicPuberService puberService;

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


        // 从消息队列中，一个个弹出消息，逐个处理，这样才能多线程并行处理。
        List<Object> list = SyncQueueObjectMap.inst().popup(RedisTopicConstant.model_channel, false, 1000);
        for (Object object : list) {
            if (!(object instanceof ChannelRespondVO)) {
                continue;
            }

            ChannelRespondVO channelRespondVO = (ChannelRespondVO) object;

            // 对数据进行解码处理
            this.decodeEvent(channelRespondVO);
        }
    }


    /**
     * 单步操作
     */
    private void decodeEvent(ChannelRespondVO channelRespondVO) {
        String channelType = channelRespondVO.getType();
        String channelName = channelRespondVO.getName();
        Object recv = channelRespondVO.getRecv();

        // 获得使用该通道名称的设备详细信息
        List<DeviceEntity> deviceEntityList = this.entityService.getDeviceEntityList(channelType, channelName);
        for (DeviceEntity entity : deviceEntityList) {
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
                this.puberService.sendReportVO(taskRespondVO);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }

    }

}
