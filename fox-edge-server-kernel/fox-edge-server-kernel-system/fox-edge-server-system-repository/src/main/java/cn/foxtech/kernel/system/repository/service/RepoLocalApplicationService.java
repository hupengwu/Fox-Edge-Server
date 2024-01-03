package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 应用的整体管理
 */
@Component
public class RepoLocalApplicationService {
    @Autowired
    private RepoLocalAppConfService appConfigService;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RepoLocalCompConvert compConvert;

    @Autowired
    private RepoLocalCompBuilder compBuilder;

    @Autowired
    private RedisConsoleService logger;

    public void syncRepoCompEntity4Application(String appType) throws IOException {
        File file = new File("");
        List<Map<String, Object>> appList = this.appConfigService.readConfFile(file.getAbsolutePath(), appType);

        // 构造：本地仓库的jar-decoder组件源
        Map<String, RepoCompEntity> dstEntityMap = new HashMap<>();
        for (Map<String, Object> appMap : appList) {
            try {
                Map<String, Object> localMap = this.compConvert.convertConf2Local(appMap);
                RepoCompEntity compEntity = this.compBuilder.buildCompEntity(localMap);

                dstEntityMap.put(compEntity.makeServiceKey(), compEntity);
            } catch (Exception e) {
                this.logger.error("从配置文件中构造参数失败!" + e.getMessage());
            }
        }

        Map<String, RepoCompEntity> srcEntityMap = new HashMap<>();
        this.entityManageService.getEntityList(RepoCompEntity.class, (Object value) -> {
            RepoCompEntity compEntity = (RepoCompEntity) value;

            if (!RepoCompVOFieldConstant.value_comp_repo_local.equals(compEntity.getCompRepo())) {
                return false;
            }
            if (!RepoCompVOFieldConstant.value_comp_type_app_service.equals(compEntity.getCompType())) {
                return false;
            }
            if (!appType.equals(compEntity.getCompParam().get(ServiceVOFieldConstant.field_app_type))) {
                return false;
            }

            srcEntityMap.put(compEntity.makeServiceKey(), compEntity);
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

        // 更新compParam中的conf属性，这个属性是这边维护的
        for (String key : eqlList) {
            RepoCompEntity srcEntity = srcEntityMap.get(key);
            RepoCompEntity dstEntity = dstEntityMap.get(key);

            StringBuilder srcSB = new StringBuilder();
            srcSB.append(srcEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_app_name, ""));
            srcSB.append(srcEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_app_type, ""));
            srcSB.append(srcEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_file_name, ""));
            srcSB.append(srcEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_loader_name, ""));
            srcSB.append(srcEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_spring_param, ""));
            srcSB.append(srcEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_conf_files, new ArrayList<>()));

            StringBuilder dstSB = new StringBuilder();
            dstSB.append(dstEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_app_name, ""));
            dstSB.append(dstEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_app_type, ""));
            dstSB.append(dstEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_file_name, ""));
            dstSB.append(dstEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_loader_name, ""));
            dstSB.append(dstEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_spring_param, ""));
            dstSB.append(dstEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_conf_files, new ArrayList<>()));

            if (dstSB.toString().equals(srcSB.toString())) {
                continue;
            }

            // 修改内容
            srcEntity.getCompParam().put(ServiceVOFieldConstant.field_app_name, dstEntity.getCompParam().get(ServiceVOFieldConstant.field_app_name));
            srcEntity.getCompParam().put(ServiceVOFieldConstant.field_app_type, dstEntity.getCompParam().get(ServiceVOFieldConstant.field_app_type));
            srcEntity.getCompParam().put(ServiceVOFieldConstant.field_file_name, dstEntity.getCompParam().get(ServiceVOFieldConstant.field_file_name));
            srcEntity.getCompParam().put(ServiceVOFieldConstant.field_loader_name, dstEntity.getCompParam().get(ServiceVOFieldConstant.field_loader_name));
            srcEntity.getCompParam().put(ServiceVOFieldConstant.field_spring_param, dstEntity.getCompParam().get(ServiceVOFieldConstant.field_spring_param));
            srcEntity.getCompParam().put(ServiceVOFieldConstant.field_conf_files, dstEntity.getCompParam().get(ServiceVOFieldConstant.field_conf_files));

            this.entityManageService.updateEntity(srcEntity);
        }
    }

    public List<BaseEntity> sort(List<BaseEntity> entityList) {
        List<BaseEntity> resultList = new ArrayList<>();


        List<BaseEntity> kernelList = new ArrayList<>();
        List<BaseEntity> systemList = new ArrayList<>();
        List<BaseEntity> serviceList = new ArrayList<>();
        for (BaseEntity entity : entityList) {
            RepoCompEntity compEntity = (RepoCompEntity) entity;
            if (!RepoCompVOFieldConstant.value_comp_type_app_service.equals(compEntity.getCompType())) {
                resultList.add(compEntity);
            }

            String appType = (String) compEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_app_type, "");
            if (ServiceVOFieldConstant.field_type_kernel.equals(appType)) {
                kernelList.add(compEntity);
                continue;
            }
            if (ServiceVOFieldConstant.field_type_system.equals(appType)) {
                systemList.add(compEntity);
                continue;
            }
            if (ServiceVOFieldConstant.field_type_service.equals(appType)) {
                serviceList.add(compEntity);
                continue;
            }
        }

        resultList.addAll(kernelList);
        resultList.addAll(systemList);
        resultList.addAll(serviceList);

        return resultList;
    }
}
