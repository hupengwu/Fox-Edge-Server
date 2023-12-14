package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateMethodEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RepoLocalJarFileCompScanner {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RepoLocalJarFileNameService fileNameService;

    @Autowired
    private RepoLocalJarFileInfoService jarFileInfoService;

    @Autowired
    private RepoLocalJarFileNameService jarFileNameService;


    public void scanRepoCompEntity(Map<String, RepoCompEntity> dstEntityMap) {
        Map<String, RepoCompEntity> srcEntityMap = new HashMap<>();
        this.entityManageService.getEntityList(RepoCompEntity.class, (Object value) -> {
            RepoCompEntity compEntity = (RepoCompEntity) value;

            if (!RepoCompVOFieldConstant.value_comp_repo_local.equals(compEntity.getCompRepo())) {
                return false;
            }
            if (!RepoCompVOFieldConstant.value_comp_type_jar_decoder.equals(compEntity.getCompType())) {
                return false;
            }

            String fileName = (String) compEntity.getCompParam().get(RepoCompConstant.filed_file_name);
            if (fileName == null) {
                return false;
            }

            srcEntityMap.put(fileName, compEntity);
            return true;
        });

        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(srcEntityMap.keySet(), dstEntityMap.keySet(), addList, delList, eqlList);

        // 只新增，不主动删除。因为operate是用户手动维护的
        for (String key : addList) {
            BaseEntity srcEntity = dstEntityMap.get(key);

            this.entityManageService.insertEntity(srcEntity);
        }

        // 更新compParam中的file属性，这个属性是这边维护的
        for (String key : eqlList) {
            RepoCompEntity srcEntity = srcEntityMap.get(key);
            RepoCompEntity dstEntity = dstEntityMap.get(key);


            String srcManufacturer = (String) srcEntity.getCompParam().getOrDefault(OperateVOFieldConstant.field_manufacturer, "");
            String dstManufacturer = (String) dstEntity.getCompParam().getOrDefault(OperateVOFieldConstant.field_manufacturer, "");

            String srcDeviceType = (String) srcEntity.getCompParam().getOrDefault(OperateVOFieldConstant.field_device_type, "");
            String dstDeviceType = (String) dstEntity.getCompParam().getOrDefault(OperateVOFieldConstant.field_device_type, "");

            if (dstDeviceType.equals(srcDeviceType) && dstManufacturer.equals(srcManufacturer)) {
                continue;
            }


            // 修改内容
            srcEntity.getCompParam().put(OperateVOFieldConstant.field_manufacturer, dstManufacturer);
            srcEntity.getCompParam().put(OperateVOFieldConstant.field_device_type, dstDeviceType);

            this.entityManageService.updateEntity(srcEntity);
        }
    }

    public Map<String, RepoCompEntity> scanRepoCompEntity() {
        // 从文件扫描的结果中，生成缺省组件实体
        Map<String, RepoCompEntity> jarFileMap = this.scanRepoCompEntityByJarName();

        // 从方法的扫描结果中，生成优先级更高的组件实体
        Map<String, RepoCompEntity> jarMethodMap = this.scanRepoCompEntityByJarMethod();

        // 用jarMethod覆盖jarFile
        jarFileMap.putAll(jarMethodMap);
        return jarFileMap;
    }

    public Map<String, RepoCompEntity> scanRepoCompEntityByJarName() {
        Map<String, RepoCompEntity> fileNameMap = new HashMap<>();

        // 获得jar文件的信息
        List<Map<String, Object>> jarInfoList = this.jarFileInfoService.findJarNameList();
        for (Map<String, Object> jarInfo : jarInfoList) {
            // 取出文件名和JAR文件版本信息
            String fileName = (String) jarInfo.get("fileName");
            String manufacturer = "Fox-Edge";
            String deviceType = "public";
            if (MethodUtils.hasEmpty(fileName)) {
                continue;
            }

            // 从结构化的文件名中，取出信息
            Map<String, String> map = this.jarFileNameService.splitJarFileName(fileName);
            if (map == null) {
                continue;
            }

            String modelName = map.get(RepoCompConstant.filed_model_name);
            String modelVersion = map.get(RepoCompConstant.filed_model_version);
            if (MethodUtils.hasEmpty(modelName, modelVersion)) {
                continue;
            }

            // 构造成对象
            RepoCompEntity compEntity = new RepoCompEntity();
            compEntity.setCompRepo(RepoCompVOFieldConstant.value_comp_repo_local);
            compEntity.setCompType(RepoCompVOFieldConstant.value_comp_type_jar_decoder);
            compEntity.setCompName(fileName);

            compEntity.getCompParam().put(OperateVOFieldConstant.field_manufacturer, manufacturer);
            compEntity.getCompParam().put(OperateVOFieldConstant.field_device_type, deviceType);
            compEntity.getCompParam().put(RepoCompConstant.filed_model_name, modelName);
            compEntity.getCompParam().put(RepoCompConstant.filed_model_version, modelVersion);
            compEntity.getCompParam().put(RepoCompConstant.filed_file_name, fileName);

            fileNameMap.put(fileName, compEntity);
        }

        return fileNameMap;
    }

    public Map<String, RepoCompEntity> scanRepoCompEntityByJarMethod() {
        List<BaseEntity> operateEntityList = this.entityManageService.getEntityList(OperateMethodEntity.class);

        Map<String, RepoCompEntity> fileNameMap = new HashMap<>();
        for (BaseEntity entity : operateEntityList) {
            OperateMethodEntity operateEntity = (OperateMethodEntity) entity;

            String fileName = (String) operateEntity.getEngineParam().getOrDefault(DeviceMethodVOFieldConstant.field_file, "");

            // 分拆xxx.v1.jar格式的文件信息
            Map<String, String> jarFileMap = this.fileNameService.splitJarFileName(fileName);
            if (jarFileMap == null) {
                continue;
            }

            String modelName = jarFileMap.get(RepoCompConstant.filed_model_name);
            String modelVersion = jarFileMap.get(RepoCompConstant.filed_model_version);
            if (MethodUtils.hasEmpty(modelName, modelVersion)) {
                continue;
            }

            RepoCompEntity compEntity = new RepoCompEntity();
            compEntity.setCompRepo(RepoCompVOFieldConstant.value_comp_repo_local);
            compEntity.setCompType(RepoCompVOFieldConstant.value_comp_type_jar_decoder);
            compEntity.setCompName(fileName);

            compEntity.getCompParam().put(OperateVOFieldConstant.field_manufacturer, operateEntity.getManufacturer());
            compEntity.getCompParam().put(OperateVOFieldConstant.field_device_type, operateEntity.getDeviceType());
            compEntity.getCompParam().put(RepoCompConstant.filed_file_name, fileName);
            compEntity.getCompParam().putAll(jarFileMap);

            fileNameMap.put(fileName, compEntity);
        }

        return fileNameMap;
    }

}
