package cn.foxtech.manager.system.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.utils.ServiceIniFilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessLoadService {
    @Autowired
    private EntityManageService entityManageService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    public void extendStartConfig(List<Map<String, Object>> serviceIniFileInfoList) {
        // 获得配置信息
        String configName = "serviceStartConfig";
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, configName);
        if (configEntity == null) {
            configEntity = new ConfigEntity();
            configEntity.setServiceName(this.foxServiceName);
            configEntity.setServiceType(this.foxServiceType);
            configEntity.setConfigName(configName);
            configEntity.getConfigValue().put("services", new ArrayList<>());
            this.entityManageService.insertEntity(configEntity);
        }

        // 从配置信息中，取出启动列表信息
        List<Map<String, Object>> serviceList = (List<Map<String, Object>>) configEntity.getConfigValue().get("services");
        if (serviceList == null) {
            serviceList = new ArrayList<>();
        }

        // 组织陈MAP，简化查询
        Map<String, Map<String, Object>> appName2ConfigStatus = ContainerUtils.buildMapByMapAt(serviceList, ServiceVOFieldConstant.field_app_name, String.class);

        String loadConfigName = ServiceVOFieldConstant.field_app_load;
        for (Map<String, Object> serviceIniFileInfo : serviceIniFileInfoList) {
            //根据fileName取出shell的基本数据
            String appName = (String) serviceIniFileInfo.get(ServiceVOFieldConstant.field_app_name);
            if (appName == null || appName.isEmpty()) {
                continue;
            }

            //根据fileName取出启动配置的扩展数据，如果没有则默认为true
            Map<String, Object> config = appName2ConfigStatus.get(appName);
            if (config != null) {
                Boolean load = Boolean.TRUE.equals(config.get(ServiceVOFieldConstant.field_app_load));
                serviceIniFileInfo.put(loadConfigName, load);
            } else {
                serviceIniFileInfo.put(loadConfigName, Boolean.TRUE);
            }
        }
    }


    public Boolean queryServiceLoad(String appName, String appType, Boolean defaultValue) {
        // 获得启动配置
        String configName = "serviceStartConfig";
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(appName, appType, configName);
        if (configEntity == null) {
            return defaultValue;
        }

        // 从配置信息中，取出启动列表信息
        List<Map<String, Object>> serviceList = (List<Map<String, Object>>) configEntity.getConfigValue().get("services");
        if (serviceList == null) {
            return defaultValue;
        }

        // 组织陈MAP，简化查询
        Map<String, Map<String, Object>> appName2ConfigStatus = ContainerUtils.buildMapByMapAt(serviceList, ServiceVOFieldConstant.field_app_name, String.class);
        Map<String, Object> configStatus = appName2ConfigStatus.get(appName);
        if (configStatus == null) {
            return defaultValue;
        }

        return (Boolean) configStatus.get(ServiceVOFieldConstant.field_app_load);
    }

    public void saveServiceLoad(String appName, String appType, Boolean appLoad) {
        try {
            // 简单校验参数
            if (MethodUtils.hasEmpty(appName, appType, appLoad)) {
                throw new ServiceException("参数不能为空:appName, appType, appLoad");
            }

            List<Map<String, Object>> iniFileInfoList = ServiceIniFilesUtils.getConfFileInfoList();
            this.extendStartConfig(iniFileInfoList);
            for (Map<String, Object> map : iniFileInfoList) {
                if (!appName.equals(map.get(ServiceVOFieldConstant.field_app_name)) || !appType.equals(map.get(ServiceVOFieldConstant.field_app_type))) {
                    continue;
                }
                if (ServiceVOFieldConstant.field_type_kernel.equals(appType)) {
                    continue;
                }

                // 获得启动配置
                String configName = "serviceStartConfig";
                ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, configName);

                // 从配置信息中，取出启动列表信息
                List<Map<String, Object>> serviceList = (List<Map<String, Object>>) configEntity.getConfigValue().get("services");
                if (serviceList == null) {
                    serviceList = new ArrayList<>();
                }

                // 组织陈MAP，简化查询
                Map<String, Map<String, Object>> appName2ConfigStatus = ContainerUtils.buildMapByMapAt(serviceList, ServiceVOFieldConstant.field_app_name, String.class);

                //根据fileName取出启动配置的扩展数据，如果没有则默认为true
                Map<String, Object> config = appName2ConfigStatus.computeIfAbsent(appName, k -> new HashMap<>());
                config.put(ServiceVOFieldConstant.field_app_name, appName);
                config.put(ServiceVOFieldConstant.field_app_load, appLoad);

                serviceList = new ArrayList<>();
                serviceList.addAll(appName2ConfigStatus.values());
                configEntity.getConfigValue().put("services", serviceList);
                this.entityManageService.updateEntity(configEntity);

            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
