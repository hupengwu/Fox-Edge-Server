package cn.foxtech.manager.system.task;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.EntityConfigManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ConfigEntityTask extends PeriodTask {
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityConfigManager entityConfigManager;

    @Autowired
    private EntityManageService entityManageService;

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
        return 10;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            List<Map<String, Object>> dataList = this.entityConfigManager.getConfigEntity();
            for (Map<String, Object> map : dataList) {
                String serviceType = (String) map.get(RedisStatusConstant.field_service_type);
                String serviceName = (String) map.get(RedisStatusConstant.field_service_name);
                Map<String, Object> configMap = (Map<String, Object>) map.get(RedisStatusConstant.field_config_entity);
                for (String key : configMap.keySet()) {
                    Object config = configMap.get(key);
                    if (!(config instanceof Map)) {
                        continue;
                    }

                    ConfigEntity configEntity = this.entityManageService.getConfigEntity(serviceName, serviceType, key);
                    if (configEntity == null) {
                        ConfigEntity newConfig = new ConfigEntity();
                        newConfig.setServiceType(serviceType);
                        newConfig.setServiceName(serviceName);
                        newConfig.setConfigName(key);
                        newConfig.setRemark("");
                        newConfig.getConfigValue().putAll((Map) config);

                        this.entityManageService.insertEntity(newConfig);
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}