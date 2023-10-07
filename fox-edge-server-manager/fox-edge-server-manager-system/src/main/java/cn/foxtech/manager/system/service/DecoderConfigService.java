package cn.foxtech.manager.system.service;

import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.manager.system.constants.RepoComponentConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 解码器启动配置服务
 */
@Component
public class DecoderConfigService {

    @Autowired
    private ManageConfigService manageConfigService;

    private Map<String, Object> getConfigValue() {
        Map<String, Object> configValue = this.manageConfigService.getConfigValue("device-service", "system", "decoderConfig");
        return configValue;
    }

    private void saveConfigValue(Map<String, Object> configValue) {
        this.manageConfigService.saveConfigValue("device-service", "system", "decoderConfig", configValue);
    }

    private String getPackName(String jarFileName) {
        Map<String, String> map = this.split(jarFileName);
        if (map == null) {
            return "";
        }

        return map.get(RepoComponentConstant.filed_model_name);
    }

    /**
     * 将jar文件名称，拆分为包名称和版本号，两个部分
     * 例如：fox-edge-server-protocol-core.v1.jar，拆分为fox-edge-server-protocol-core和v1
     *
     * @param jarFileName jar文件名称，例如fox-edge-server-protocol-core.v1.jar
     * @return decoder，v1
     */
    public Map<String, String> split(String jarFileName) {
        // 检查：是否为。jar文件
        if (!jarFileName.toLowerCase().endsWith(".jar")) {
            return null;
        }

        // 去除.jar的后缀，为拆分数据做准备
        jarFileName = jarFileName.substring(0, jarFileName.length() - ".jar".length());
        String[] items = jarFileName.split("\\.");
        if (items.length < 2) {
            return null;
        }

        String modelVersion = items[items.length - 1];
        String modelName = jarFileName.substring(0, jarFileName.length() - modelVersion.length() - 1);

        Map<String, String> result = new HashMap<>();
        result.put(RepoComponentConstant.filed_model_name, modelName);
        result.put(RepoComponentConstant.filed_model_version, modelVersion);

        return result;
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
