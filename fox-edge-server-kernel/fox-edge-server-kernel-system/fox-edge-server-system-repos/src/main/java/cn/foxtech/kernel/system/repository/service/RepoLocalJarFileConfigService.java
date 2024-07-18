package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.kernel.system.common.service.ManageConfigService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 本地JAR文件装载配置
 */
@Component
public class RepoLocalJarFileConfigService {

    @Autowired
    private ManageConfigService manageConfigService;

    private Map<String, Object> getConfigValue() {
        Map<String, Object> configValue = this.manageConfigService.getConfigValue("device-service", "system", "decoderConfig");
        return configValue;
    }

    private void saveConfigValue(Map<String, Object> configValue) {
        this.manageConfigService.saveConfigValue("device-service", "system", "decoderConfig", configValue);
    }

    public Set<String> getLoads() {
        Map<String, Object> configValue = this.getConfigValue();

        Set<String> result = new HashSet<>();
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) configValue.getOrDefault(DeviceDecoderVOFieldConstant.field_list, new ArrayList<>());
        for (Map<String, Object> data : dataList) {
            String fileName = (String) data.get("fileName");
            if (fileName == null) {
                continue;
            }

            Boolean load = (Boolean) data.get("load");
            if (!Boolean.TRUE.equals(load)) {
                continue;
            }


            result.add(fileName);
        }

        return result;
    }

    public void updateConfig(String fileName, Boolean load) {
        // 获得数据库中的配置
        Map<String, Object> configValue = this.getConfigValue();

        // 取出列表数据
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) configValue.get(DeviceDecoderVOFieldConstant.field_list);

        // 修改数值
        this.setConfigValue(fileName, load, dataList);

        // 保存数据
        this.saveConfigValue(configValue);
    }

    private void setConfigValue(String fileName, Boolean load, List<Map<String, Object>> dataList) {
        for (Map<String, Object> entity : dataList) {
            String fileNameValue = (String) entity.get(DeviceDecoderVOFieldConstant.field_file_name);

            if (fileNameValue.equals(fileName)) {
                Long time = System.currentTimeMillis();
                entity.put(DeviceDecoderVOFieldConstant.field_file_name, fileName);
                entity.put(DeviceDecoderVOFieldConstant.field_load, load);
                entity.put(DeviceDecoderVOFieldConstant.field_create_time, time);
                entity.put(DeviceDecoderVOFieldConstant.field_update_time, time);
                return;
            }
        }

        Long time = System.currentTimeMillis();
        Map<String, Object> entity = new HashMap<>();
        entity.put(DeviceDecoderVOFieldConstant.field_file_name, fileName);
        entity.put(DeviceDecoderVOFieldConstant.field_load, load);
        entity.put(DeviceDecoderVOFieldConstant.field_create_time, time);
        entity.put(DeviceDecoderVOFieldConstant.field_update_time, time);

        dataList.add(entity);

        return;
    }
}
