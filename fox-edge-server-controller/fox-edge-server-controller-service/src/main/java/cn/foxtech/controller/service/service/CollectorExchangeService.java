package cn.foxtech.controller.service.service;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskEntity;
import cn.foxtech.common.rpc.redis.persist.client.RedisListPersistClient;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.controller.common.service.DeviceOperateService;
import cn.foxtech.controller.common.service.EntityManageService;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 主从问答方式设备的数据采集
 * <p>
 * 背景知识：主从半双工设备，这类设备只会被动响应上位机的命令请求。现实中大多数简单的工控设备都是这种设备<br>
 */
@Component
public class CollectorExchangeService extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(CollectorExchangeService.class);
    /**
     * 任务最近执行时间
     */
    private final Map<String, Long> lastTimeMap = new HashMap<>();

    @Autowired
    private EntityManageService entityManageService;
    @Autowired
    private RedisListPersistClient persistClient;
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
        Thread.sleep(100);

        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            return;
        }

        // 检测：持久化服务接收队列是否堵塞了
        if (this.persistClient.isValueRequestBlock()) {
            return;
        }

        List<BaseEntity> taskList = this.entityManageService.getEntityList(OperateMonitorTaskEntity.class);
        for (BaseEntity entity : taskList) {
            OperateMonitorTaskEntity taskEntity = (OperateMonitorTaskEntity) entity;

            long timeInterval = this.getTimeInterval(taskEntity);

            // 检查：是否到了执行周期
            long lastTime = this.lastTimeMap.getOrDefault(taskEntity.getTemplateName(), 0L);
            if (!this.testLastTime(timeInterval, lastTime)) {
                continue;
            }
            long startTime = System.currentTimeMillis();
            this.lastTimeMap.put(taskEntity.getTemplateName(), startTime);

            // 组织跟该任务相关的设备ID
            List<Object> deviceIds = taskEntity.getDeviceIds();
            if (deviceIds.isEmpty()) {
                continue;
            }

            for (int index = 0; index < deviceIds.size(); index++) {
                Object objectId = deviceIds.get(index);
                Long deviceId = NumberUtils.makeLong(objectId);
                if (deviceId == null) {
                    continue;
                }

                DeviceEntity deviceEntity = this.entityManageService.getEntity(deviceId, DeviceEntity.class);
                if (deviceEntity == null) {
                    continue;
                }

                // 均摊CPU的损耗
                this.sleep(startTime, timeInterval, deviceIds.size(), index);

                // 执行任务
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

            // 检查：下游的持久化队列，是否已经进入50%的繁忙状态
            if (this.persistClient.isValueRequestBusy(50)) {
                Thread.sleep(100);
            }

            // 更新设备消息到数据库和redis
            this.persistClient.pushValueRequest(taskRespondVO);
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
                OperateRequestVO operateRequestVO = OperateRequestVO.buildOperateRequestVO(param);
                operateRequestVO.setDeviceType(deviceEntity.getDeviceType());
                operateRequestVO.setManufacturer(deviceEntity.getManufacturer());
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

    /**
     * 均摊CPU利用率的算法
     *
     * @param timeInterval
     * @param deviceCount
     * @throws InterruptedException
     */
    private void sleep(long startTime, long timeInterval, int deviceCount, int index) throws InterruptedException {
        boolean average = false;
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "serverConfig");
        if (configEntity != null && configEntity.getConfigValue().containsKey("average")) {
            average = (Boolean) configEntity.getConfigValue().get("average");
        }

        if (!average) {
            return;
        }

        // 检查：是否需要休眠
        long step = System.currentTimeMillis() - startTime;
        long unit = timeInterval / deviceCount;
        if (step < index * unit) {
            Thread.sleep(unit);
        }
    }

    private boolean testLastTime(long timeInterval, long lastTime) {
        if (timeInterval == -1) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        return currentTime - lastTime > timeInterval;
    }

    private long getTimeInterval(OperateMonitorTaskEntity taskEntity) {
        try {
            String timeMode = (String) taskEntity.getTaskParam().get("timeMode");
            String timeUnit = (String) taskEntity.getTaskParam().get("timeUnit");
            Integer timeInterval = (Integer) taskEntity.getTaskParam().get("timeInterval");

            if (MethodUtils.hasEmpty(timeMode, timeUnit, timeInterval)) {
                return -1;
            }

            if (timeMode.equals("interval")) {
                if (timeUnit.equals("second")) {
                    return timeInterval * 1000;
                }
                if (timeUnit.equals("minute")) {
                    return timeInterval * 1000 * 60;
                }
                if (timeUnit.equals("hour")) {
                    return timeInterval * 1000 * 3600;
                }
                if (timeUnit.equals("day")) {
                    return timeInterval * 1000 * 3600 * 24;
                }
            }

            return -1;

        } catch (Exception e) {
            return -1;
        }
    }
}
