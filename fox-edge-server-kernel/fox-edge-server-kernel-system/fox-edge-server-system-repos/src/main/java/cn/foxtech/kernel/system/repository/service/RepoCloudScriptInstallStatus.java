package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.md5.MD5Utils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.constants.RepoStatusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安装状态的管理
 */
@Component
public class RepoCloudScriptInstallStatus {
    /**
     * 本地组件的安装状态列表：通过缓存安装状态，用于优化磁盘扫描安装包的长时间卡顿问题
     */
    private final Map<String, Object> statusMap = new ConcurrentHashMap<>();


    @Autowired
    private RepoLocalScriptService modelService;

    public void extend(List<Map<String, Object>> mapList) {
        for (Map<String, Object> comp : mapList) {
            String manufacturer = (String) comp.get(RepoCompConstant.filed_manufacturer);
            String deviceType = (String) comp.get(RepoCompConstant.filed_device_type);
            if (MethodUtils.hasEmpty(manufacturer, deviceType)) {
                continue;
            }

            // 云端的最新版本信息
            Map<String, Object> lastVersion = (Map<String, Object>) comp.get(RepoCompConstant.filed_last_version);
            if (lastVersion == null) {
                continue;
            }

            String cloudId = (String) lastVersion.get("id");
            String cloudMd5 = (String) lastVersion.get("md5");
            if (cloudId == null || cloudMd5 == null) {
                continue;
            }

            // 扫描本地状态：计算并保存
            this.scanStatus(manufacturer, deviceType, cloudId, cloudMd5);

            // 取出本地状态
            Integer status = (Integer) MapUtils.getValue(this.statusMap, manufacturer, deviceType, RepoCompConstant.filed_status);
            if (status == null) {
                continue;
            }

            lastVersion.put("status", status);
        }
    }


    private void scanStatus(String manufacturer, String deviceType, String cloudId, String cloudMd5) {
        // 简单验证
        if (MethodUtils.hasEmpty(manufacturer, deviceType)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType");
        }

        // 阶段1：未下载
        int status = RepoStatusConstant.status_not_downloaded;

        // 场景1：本地没有组件信息
        RepoCompEntity repoCompEntity = this.modelService.getCompEntity(manufacturer, deviceType);
        if (repoCompEntity == null) {
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, RepoCompConstant.filed_status, status);
            return;
        }

        // 场景2：本地没有来自云端的安装信息
        Map<String, Object> installVersion = (Map<String, Object>) repoCompEntity.getCompParam().get("installVersion");
        if (installVersion == null) {
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, RepoCompConstant.filed_status, status);
            return;
        }

        // 场景3：本地来自云端的信息，不完整
        if (installVersion.get("id") == null || installVersion.get("updateTime") == null) {
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, RepoCompConstant.filed_status, status);
            return;
        }

        // 阶段5："已下载，已安装!"
        status = RepoStatusConstant.status_installed;
        MapUtils.setValue(this.statusMap, manufacturer, deviceType, RepoCompConstant.filed_status, status);

        // 阶段6：检查是否待升级
        if (cloudId.equals(installVersion.get("id"))) {
            if (!this.getMD5Txt(manufacturer, deviceType).equals(cloudMd5)) {
                status = RepoStatusConstant.status_damaged_package;
                MapUtils.setValue(this.statusMap, manufacturer, deviceType, RepoCompConstant.filed_status, status);

            }
        }

        // 阶段7：检查是否待升级
        if (!cloudId.equals(installVersion.get("id"))) {
            status = RepoStatusConstant.status_need_upgrade;
            MapUtils.setValue(this.statusMap, manufacturer, deviceType, RepoCompConstant.filed_status, status);
        }
    }

    private String getMD5Txt(String manufacturer, String deviceType) {
        try {
            List<OperateEntity> entityList = this.modelService.getOperateEntityList(manufacturer, deviceType);
            String txt = this.getOrderText(entityList);
            String md5 = MD5Utils.getMD5Txt(txt);
            return md5;
        } catch (Exception e) {
            return "";
        }
    }

    private String getOrderText(List<OperateEntity> list) throws InstantiationException, IllegalAccessException {
        list.sort(new Comparator<OperateEntity>() {
            @Override
            public int compare(OperateEntity v1, OperateEntity v2) {
                String name1 = v1.getOperateName();
                String name2 = v2.getOperateName();
                return name1.compareTo(name2);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (OperateEntity entity : list) {
            sb.append(this.getOrderText(entity));
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
    private String getOrderText(OperateEntity entity) throws InstantiationException, IllegalAccessException {
        // 注意：不要调整代码顺序，这是跟fox-cloud保持一致的算法，两边要同步修改
        List<Object> values = new ArrayList<>();
        values.add(entity.getOperateName());
        values.add(entity.getServiceType());
        values.add(entity.getOperateMode());
        values.add(entity.getEngineType());
        values.add(entity.getTimeout());
        values.add(MapUtils.castMap(entity.getEngineParam(), TreeMap.class));
        values.add(MapUtils.castMap(entity.getExtendParam(), TreeMap.class));

        return values.toString();
    }


}
