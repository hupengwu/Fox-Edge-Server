package cn.foxtech.trigger.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.TriggerMethodEntity;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.trigger.logic.common.FoxEdgeTrigger;
import cn.foxtech.trigger.logic.common.FoxEdgeTriggerTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MethodEntityService {
    private static final Logger logger = Logger.getLogger(MethodEntityService.class);

    @Autowired
    private EntityManageService entityManageService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    /**
     * 扫描解码器
     */
    public void scanJarFile() {
        // 读取解码器配置信息
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "triggerConfig");
        if (configEntity == null) {
            logger.error("找不到triggerConfig的配置信息");
            return;
        }

        List<Map<String, Object>> configList = (List<Map<String, Object>>) configEntity.getConfigValue().get("list");

        // 取出需要加载的文件名
        List<String> jarFileList = new ArrayList<>();
        for (Map<String, Object> map : configList) {
            String fileName = (String) map.get("fileName");
            Boolean load = (Boolean) map.get("load");
            if (MethodUtils.hasEmpty(fileName, load)) {
                continue;
            }

            if (load) {
                jarFileList.add("jar/trigger/" + fileName);
            }
        }

        // 扫描jar文件包
        FoxEdgeTriggerTemplate.inst().scanMethodPair(jarFileList);

    }

    /**
     * 将解码器信息更新到redis中
     */
    public void updateEntityList() {
        List<BaseEntity> entityList = this.buildMethodEntityList();

        // 新增不存在的数据
        for (BaseEntity entity : entityList) {
            TriggerMethodEntity triggerMethodEntity = this.entityManageService.getEntity(entity.makeServiceKey(), TriggerMethodEntity.class);
            if (triggerMethodEntity == null) {
                this.entityManageService.insertRDEntity(entity);
            }
        }

        Map<String, BaseEntity> entityMap = ContainerUtils.buildMapByKeyAndFinalMethod(entityList, BaseEntity::makeServiceKey, String.class);
        List<BaseEntity> existList = this.entityManageService.getEntityList(TriggerMethodEntity.class);
        for (BaseEntity exist : existList) {
            TriggerMethodEntity entity = (TriggerMethodEntity) entityMap.get(exist.makeServiceKey());
            if (entity != null) {
                if (!entity.makeServiceValue().equals(exist.makeServiceValue())) {
                    this.entityManageService.updateRDEntity(entity);
                }
            } else {
                this.entityManageService.deleteRDEntity(exist.makeServiceKey(), TriggerMethodEntity.class);
            }
        }
    }


    /**
     * 构造TriggerMethodEntity信息
     */
    public List<BaseEntity> buildMethodEntityList() {
        Long time = System.currentTimeMillis();

        Map<String, Map<String, FoxEdgeTrigger>> allTriggers = FoxEdgeTriggerTemplate.inst().getTriggers();

        List<BaseEntity> resultList = new ArrayList<>();
        for (Map.Entry<String, Map<String, FoxEdgeTrigger>> deviceEntity : allTriggers.entrySet()) {
            Map<String, FoxEdgeTrigger> methodpairs = deviceEntity.getValue();
            for (Map.Entry<String, FoxEdgeTrigger> entity : methodpairs.entrySet()) {
                FoxEdgeTrigger methodPair = entity.getValue();

                TriggerMethodEntity methodEntity = new TriggerMethodEntity();

                methodEntity.setManufacturer(methodPair.getManufacturer());
                methodEntity.setMethodName(methodPair.getMethodName());
                methodEntity.setModelName(methodPair.getModelName());

                methodEntity.setCreateTime(time);
                methodEntity.setUpdateTime(time);

                resultList.add(methodEntity);
            }
        }

        return resultList;
    }
}
