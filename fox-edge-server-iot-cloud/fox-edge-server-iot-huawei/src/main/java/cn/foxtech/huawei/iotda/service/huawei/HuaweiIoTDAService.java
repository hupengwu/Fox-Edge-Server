package cn.foxtech.huawei.iotda.service.huawei;

import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.huawei.iotda.service.entity.model.Model;
import cn.foxtech.huawei.iotda.service.entity.model.Service;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HuaweiIoTDAService {
    private static final Logger logger = Logger.getLogger(HuaweiIoTDAService.class);
    /**
     * 从华为物联网平台，导出来的物模型文件
     */
    @Getter
    private final Map<String, Map<String, Object>> modelMap = new HashMap<>();

    @Autowired
    private RedisConsoleService consoleService;
    @Getter
    private String productId = "";
    @Getter
    private String nodeId = "";
    @Getter
    private String deviceId = "";
    @Getter
    private Integer deviceOnlinePush = 30;
    @Autowired
    private RemoteMqttService remoteMqttService;
    @Autowired
    private LocalConfigService localConfigService;

    public void initialize() {
        // 初始化MQTT组件
        this.remoteMqttService.initialize(new HuaweiMqttHandler());

        // 取出全局配置参数
        Map<String, Object> huaweiIoTDA = (Map<String, Object>) this.localConfigService.getConfigs().getOrDefault("huaweiIoTDA", new HashMap<>());

        this.productId = (String) huaweiIoTDA.getOrDefault("productId", "");
        this.nodeId = (String) huaweiIoTDA.getOrDefault("nodeId", "");
        this.deviceId = (String) huaweiIoTDA.getOrDefault("deviceId", "");
        this.deviceOnlinePush = (Integer) huaweiIoTDA.getOrDefault("deviceOnlinePush", 30);

        // 装载模型文件
        List<String> filePaths = (List<String>) huaweiIoTDA.getOrDefault("models", new ArrayList<>());
        this.loadModels(filePaths);
    }

    private void loadModels(List<String> filePaths) {
        File dir = new File("");

        for (String filePath : filePaths) {
            try {
                //  读取从华为平台导出的物模型文件
                String fileName = dir.getAbsolutePath() + "/model" + filePath;
                fileName = FileNameUtils.getOsFilePath(fileName);
                String json = FileTextUtils.readTextFile(fileName);

                Model model = JsonUtils.buildObject(json, Model.class);
                for (Service service : model.getServices()) {
                    String serviceId = service.getService_id();
                    if (serviceId == null) {
                        continue;
                    }

                    Map<String, Object> properties = this.modelMap.computeIfAbsent(serviceId, k -> new HashMap<>());
                    for (Map<String, Object> proprity : service.getProperties()) {
                        String propertyName = (String) proprity.get("property_name");
                        if (propertyName == null) {
                            continue;
                        }

                        properties.put(propertyName, proprity);

                    }
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
                this.consoleService.error(e.getMessage());
            }
        }
    }
}
