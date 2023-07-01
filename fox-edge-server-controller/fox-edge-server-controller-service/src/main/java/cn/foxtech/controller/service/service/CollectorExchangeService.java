package cn.foxtech.controller.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.controller.common.redistopic.RedisTopicPuberService;
import cn.foxtech.controller.common.service.DeviceOperateService;
import cn.foxtech.controller.common.service.EntityManageService;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskEntity;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 主从问答方式设备的数据采集<br>
 * 背景知识：主从半双工设备，这类设备只会被动响应上位机的命令请求。现实中大多数简单的工控设备都是这种设备<br>
 */
@Component
public class CollectorExchangeService extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(CollectorExchangeService.class);

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RedisTopicPuberService puberService;

    @Autowired
    private DeviceOperateService deviceOperateService;

    @Value("${spring.redis_topic.controller_model}")
    private String controllerModel = "system_controller";

    @Autowired
    private ServiceStatus serviceStatus;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    /**
     * 执行任务
     * 步骤：
     * 1.获取操作模板列表，进行循环遍历操作
     * 2.将操作请求编码后发送给设备，并对设备返回的数据进行解码，得到设备的各项数值
     * 3.将设备的各项数值，写入redis保存，通过redis分享给其他服务
     *
     * @throws Exception 异常情况
     */
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            return;
        }

        List<BaseEntity> taskList = this.entityManageService.getEntityList(OperateMonitorTaskEntity.class);
        for (BaseEntity entity : taskList) {
            OperateMonitorTaskEntity taskEntity = (OperateMonitorTaskEntity) entity;

            // 组织跟该任务相关的设备ID
            List<Object> deviceIds = taskEntity.getDeviceIds();

            for (Object objectId : deviceIds) {
                Long deviceId = NumberUtils.makeLong(objectId);
                if (deviceId == null) {
                    continue;
                }

                DeviceEntity deviceEntity = this.entityManageService.getEntity(deviceId, DeviceEntity.class);
                if (deviceEntity == null) {
                    continue;
                }

                // 获得时间间隔配置:如果没有配置，那么默认100毫秒
                Integer sleep = 100;
                ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "devicePollingConfig");
                if (configEntity != null && configEntity.getConfigParam().containsKey("sleep")) {
                    sleep = (Integer) configEntity.getConfigParam().get("sleep");
                }

                // 不能访问太快，否则CPU占用率都受不了
                Thread.sleep(sleep);

                this.executeTask(deviceEntity, taskEntity);
            }

        }
    }

    private void executeTask(DeviceEntity deviceEntity, OperateMonitorTaskEntity taskEntity) {
        try {
            // 通过时间戳，判断设备服务是否正在运行
            if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_device, RedisStatusConstant.value_model_name_device, 60 * 1000)) {
                return;
            }

            TaskRequestVO taskRequestVO = this.buildPackageRequestVO(deviceEntity, taskEntity);
            if (taskRequestVO.getRequestVOS().isEmpty()) {
                return;
            }

            // 总的超时
            if (taskRequestVO.getTimeout() > 60 * 1000) {
                taskRequestVO.setTimeout(60 * 1000);
            }

            // 将批量操作发送给设备
            TaskRespondVO taskRespondVO = this.deviceOperateService.execute(taskRequestVO);
            List<OperateRespondVO> respondVOS = taskRespondVO.getRespondVOS();
            if (respondVOS == null || respondVOS.isEmpty()) {
                return;
            }

            // 更新设备消息到数据库和redis
            this.puberService.sendRespondVO(taskRespondVO);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    /**
     * 根据操作构造包请求
     * 它的包内容为：模板中定义的操作，参数为模板参数与设备参数的合并参数
     *
     * @param deviceEntity          设备实体
     * @param operateTemplateEntity 操作模板
     * @return 包请求
     */
    private TaskRequestVO buildPackageRequestVO(DeviceEntity deviceEntity, OperateMonitorTaskEntity operateTemplateEntity) {
        // 根据模板的参数，开始构造发送给设备的批量服务请求
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.setClientName(this.controllerModel);
        taskRequestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));

        Integer totalTimeout = 0;
        for (Map<String, Object> param : operateTemplateEntity.getTemplateParam()) {
            try {
                // 单个设备的操作
                OperateRequestVO operateRequestVO = JsonUtils.buildObject(param, OperateRequestVO.class);
                operateRequestVO.setDeviceType(deviceEntity.getDeviceType());
                operateRequestVO.setDeviceName(deviceEntity.getDeviceName());
                operateRequestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));

                // 合并来自设备参数：也就是如果有重复字段，会设备参数会优先覆盖模板参数
                if (operateRequestVO.getParam() == null) {
                    operateRequestVO.setParam(new HashMap<>());
                }
                operateRequestVO.getParam().putAll(deviceEntity.getDeviceParam());

                taskRequestVO.getRequestVOS().add(operateRequestVO);

                totalTimeout += operateRequestVO.getTimeout();
            } catch (Exception e) {
                logger.error("操作模板异常:" + deviceEntity.getDeviceName(), e);
            }
        }

        taskRequestVO.setTimeout(totalTimeout);

        return taskRequestVO;
    }
}
