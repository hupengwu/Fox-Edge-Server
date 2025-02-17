/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.DeviceTemplateVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceTemplateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.md5.MD5Utils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.string.StringSort;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.constants.RepoStatusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安装状态的管理
 */
@Component
public class RepoCloudDevTemplateInstallStatus {
    /**
     * 本地组件的安装状态列表：通过缓存安装状态，用于优化磁盘扫描安装包的长时间卡顿问题
     */
    private final Map<String, Object> statusMap = new ConcurrentHashMap<>();


    @Autowired
    private RepoLocalDevTemplateService modelService;

    public void extend(List<Map<String, Object>> mapList) {
        for (Map<String, Object> comp : mapList) {
            String manufacturer = (String) comp.get(DeviceTemplateVOFieldConstant.field_manufacturer);
            String deviceType = (String) comp.get(DeviceTemplateVOFieldConstant.field_device_type);
            String subsetName = (String) comp.get(DeviceTemplateVOFieldConstant.field_subset_name);
            if (MethodUtils.hasEmpty(manufacturer, deviceType, subsetName)) {
                continue;
            }

            // 云端的最新版本信息
            Map<String, Object> lastVersion = (Map<String, Object>) comp.get(RepoCompConstant.field_last_version);
            if (lastVersion == null) {
                continue;
            }

            String cloudId = (String) lastVersion.get("id");
            String cloudMd5 = (String) lastVersion.get("md5");
            if (cloudId == null || cloudMd5 == null) {
                continue;
            }

            // 扫描本地状态：计算并保存
            this.scanStatus(manufacturer, deviceType, subsetName, cloudId, cloudMd5);

            // 取出本地状态
            Integer status = (Integer) MapUtils.getValue(this.statusMap, manufacturer, deviceType, subsetName, RepoCompConstant.field_status);
            if (status == null) {
                continue;
            }

            lastVersion.put("status", status);
        }
    }


    private void scanStatus(String manufacturer, String deviceType, String subsetName, String cloudId, String cloudMd5) {
        // 简单验证
        if (MethodUtils.hasEmpty(manufacturer, deviceType, subsetName)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType, subsetName");
        }

        // 阶段1：未下载
        int status = RepoStatusConstant.status_not_downloaded;

        // 场景1：本地没有组件信息
        RepoCompEntity repoCompEntity = this.modelService.getCompEntity(manufacturer, deviceType, subsetName);
        if (repoCompEntity == null) {
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, subsetName, RepoCompConstant.field_status, status);
            return;
        }

        // 场景2：本地没有来自云端的安装信息
        Map<String, Object> installVersion = (Map<String, Object>) repoCompEntity.getCompParam().get("installVersion");
        if (installVersion == null) {
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, subsetName, RepoCompConstant.field_status, status);
            return;
        }

        // 场景3：本地来自云端的信息，不完整
        if (installVersion.get("id") == null || installVersion.get("updateTime") == null) {
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, subsetName, RepoCompConstant.field_status, status);
            return;
        }

        // 阶段5："已下载，已安装!"
        status = RepoStatusConstant.status_installed;
        MapUtils.setValue(this.statusMap, manufacturer, deviceType, subsetName, RepoCompConstant.field_status, status);

        // 阶段6：检查是否待升级
        if (cloudId.equals(installVersion.get("id"))) {
            if (!this.getMD5Txt(manufacturer, deviceType, subsetName).equals(cloudMd5)) {
                status = RepoStatusConstant.status_damaged_package;
                MapUtils.setValue(this.statusMap, manufacturer, deviceType, subsetName, RepoCompConstant.field_status, status);

            }
        }

        // 阶段7：检查是否待升级
        if (!cloudId.equals(installVersion.get("id"))) {
            status = RepoStatusConstant.status_need_upgrade;
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, subsetName, RepoCompConstant.field_status, status);
        }
    }

    private String getMD5Txt(String manufacturer, String deviceType, String subsetName) {
        try {
            List<BaseEntity> entityList = this.modelService.getDeviceTemplateEntityList(manufacturer, deviceType, subsetName);
            String txt = this.getOrderText(entityList);
            String md5 = MD5Utils.getMD5Txt(txt);
            return md5;
        } catch (Exception e) {
            return "";
        }
    }

    private String getOrderText(List<BaseEntity> list) throws InstantiationException, IllegalAccessException {
        list.sort((v1, v2) -> {
            DeviceTemplateEntity dv1 = (DeviceTemplateEntity) v1;
            DeviceTemplateEntity dv2 = (DeviceTemplateEntity) v2;

            String name1 = dv1.getTemplateType() + ":" + dv1.getTemplateName();
            String name2 = dv2.getTemplateType() + ":" + dv2.getTemplateName();
            return name1.compareTo(name2);
        });

        StringBuilder sb = new StringBuilder();
        for (BaseEntity entity : list) {
            sb.append(this.getOrderText((DeviceTemplateEntity) entity));
            sb.append(";");
        }

        return sb.toString();
    }

    /**
     * 有序文本，避免HashMap导致的无序文本问题
     * 该算法与fox-cloud保持一致
     *
     * @param entity
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private String getOrderText(DeviceTemplateEntity entity) throws InstantiationException, IllegalAccessException {
        // 注意：不要调整代码顺序，这是跟fox-cloud保持一致的算法，两边要同步修改
        List<Object> values = new ArrayList<>();
        values.add(entity.getTemplateType());
        values.add(entity.getTemplateName());
        values.add(entity.getTemplateParam());
        values.add(entity.getExtendParam());

        return StringSort.getListString(values);
    }
}
