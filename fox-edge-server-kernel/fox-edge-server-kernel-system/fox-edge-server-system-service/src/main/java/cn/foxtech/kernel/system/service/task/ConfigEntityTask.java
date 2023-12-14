package cn.foxtech.kernel.system.service.task;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.EntityConfigManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.kernel.system.common.service.EntityManageService;
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
                    Object serverConfig = configMap.get(key);
                    if (!(serverConfig instanceof Map)) {
                        continue;
                    }

                    // 取出配置内容
                    Object remark = ((Map) serverConfig).get("remark");
                    Object configValue = ((Map) serverConfig).get("configValue");
                    Object configParam = ((Map) serverConfig).get("configParam");
                    if (MethodUtils.hasNull(remark, configValue, configParam)) {
                        continue;
                    }

                    // 检查：数据格式是否合法
                    if (!(remark instanceof String) || !(configValue instanceof Map) || !(configParam instanceof Map)) {
                        continue;
                    }

                    ConfigEntity configEntity = this.entityManageService.getConfigEntity(serviceName, serviceType, key);
                    if (configEntity == null) {
                        ConfigEntity newConfig = new ConfigEntity();
                        newConfig.setServiceType(serviceType);
                        newConfig.setServiceName(serviceName);
                        newConfig.setConfigName(key);
                        newConfig.setRemark((String) remark);
                        newConfig.getConfigValue().putAll((Map) configValue);
                        newConfig.getConfigParam().putAll((Map) configParam);


                        this.entityManageService.insertEntity(newConfig);
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}