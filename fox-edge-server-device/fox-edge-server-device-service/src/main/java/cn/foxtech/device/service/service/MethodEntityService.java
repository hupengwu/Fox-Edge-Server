package cn.foxtech.device.service.service;

import cn.foxtech.common.entity.constant.Constants;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.OperateMethodEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeExchangeMethod;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeMethodTemplate;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgePublishMethod;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.scanner.FoxEdgeMethodTemplateScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MethodEntityService {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

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
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "decoderConfig");
        if (configEntity == null) {
            logger.error("找不到decoderConfig的配置信息");
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
                jarFileList.add("jar/decoder/" + fileName);
            }
        }

        // 扫描jar文件包
        FoxEdgeMethodTemplateScanner.scanMethodPair(jarFileList);

    }

    /**
     * 将解码器信息更新到redis中
     */
    public void updateEntityList() {
        List<BaseEntity> entityList = this.buildMethodEntityList();

        // 新增不存在的数据
        for (BaseEntity entity : entityList) {
            OperateMethodEntity operateMethodEntity = this.entityManageService.getEntity(entity.makeServiceKey(), OperateMethodEntity.class);
            if (operateMethodEntity == null) {
                this.entityManageService.insertRDEntity(entity);
            }
        }

        Map<String, BaseEntity> entityMap = ContainerUtils.buildMapByKeyAndFinalMethod(entityList, BaseEntity::makeServiceKey, String.class);
        List<BaseEntity> existList = this.entityManageService.getEntityList(OperateMethodEntity.class);
        for (BaseEntity exist : existList) {
            OperateMethodEntity entity = (OperateMethodEntity) entityMap.get(exist.makeServiceKey());
            if (entity != null) {
                if (!entity.makeServiceValue().equals(exist.makeServiceValue())) {
                    this.entityManageService.updateRDEntity(entity);
                }
            } else {
                this.entityManageService.deleteRDEntity(exist.makeServiceKey(), OperateMethodEntity.class);
            }
        }
    }

    private List<BaseEntity> buildMethodEntityList() {
        Map<String, Map<String, FoxEdgeExchangeMethod>> operateMethod = FoxEdgeMethodTemplate.inst().getExchangeMethod();
        Map<String, Map<String, FoxEdgePublishMethod>> publishMethod = FoxEdgeMethodTemplate.inst().getPublishMethod();

        List<BaseEntity> methodEntityList = new ArrayList<>();
        methodEntityList.addAll(this.buildExchangeMethod(operateMethod));
        methodEntityList.addAll(this.buildPublishMethod(publishMethod));

        return methodEntityList;
    }

    /**
     * 保存OperateEntity信息
     */
    private List<OperateMethodEntity> buildExchangeMethod(Map<String, Map<String, FoxEdgeExchangeMethod>> methodPairs) {
        Long time = System.currentTimeMillis();

        List<OperateMethodEntity> resultList = new ArrayList<>();
        for (Map.Entry<String, Map<String, FoxEdgeExchangeMethod>> deviceEntity : methodPairs.entrySet()) {
            Map<String, FoxEdgeExchangeMethod> methodpairs = deviceEntity.getValue();
            for (Map.Entry<String, FoxEdgeExchangeMethod> entity : methodpairs.entrySet()) {
                FoxEdgeExchangeMethod methodPair = entity.getValue();

                OperateMethodEntity methodEntity = new OperateMethodEntity();

                methodEntity.setManufacturer(methodPair.getManufacturer());
                methodEntity.setDeviceType(methodPair.getDeviceType());
                methodEntity.setOperateName(methodPair.getName());
                methodEntity.setDataType(methodPair.getMode());
                methodEntity.setOperateMode(Constants.OPERATE_MODE_EXCHANGE);
                methodEntity.setTimeout(methodPair.getTimeout());
                methodEntity.setPolling(methodPair.isPolling());
                methodEntity.setCreateTime(time);
                methodEntity.setUpdateTime(time);

                resultList.add(methodEntity);
            }
        }

        return resultList;
    }

    /**
     * 保存OperateEntity信息
     */
    private List<OperateMethodEntity> buildPublishMethod(Map<String, Map<String, FoxEdgePublishMethod>> methodPairs) {

        Long time = System.currentTimeMillis();

        List<OperateMethodEntity> resultList = new ArrayList<>();

        for (Map.Entry<String, Map<String, FoxEdgePublishMethod>> deviceEntity : methodPairs.entrySet()) {
            Map<String, FoxEdgePublishMethod> methodpairs = deviceEntity.getValue();
            for (Map.Entry<String, FoxEdgePublishMethod> entity : methodpairs.entrySet()) {
                FoxEdgePublishMethod methodPair = entity.getValue();

                OperateMethodEntity methodEntity = new OperateMethodEntity();

                methodEntity.setManufacturer(methodPair.getManufacturer());
                methodEntity.setDeviceType(methodPair.getDeviceType());
                methodEntity.setOperateName(methodPair.getName());
                methodEntity.setOperateMode(Constants.OPERATE_MODE_PUBLISH);
                methodEntity.setTimeout(methodPair.getTimeout());
                methodEntity.setPolling(methodPair.isPolling());
                methodEntity.setCreateTime(time);
                methodEntity.setUpdateTime(time);

                resultList.add(methodEntity);
            }
        }

        return resultList;
    }
}
