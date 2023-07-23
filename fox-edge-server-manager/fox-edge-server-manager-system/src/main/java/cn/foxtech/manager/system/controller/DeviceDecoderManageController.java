package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.manager.system.service.ProcessStartService;
import cn.foxtech.manager.system.utils.ProtocolJarUtils;
import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device/decoder")
public class DeviceDecoderManageController {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ProcessStartService processStartService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceValueEntity.class);
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("entities")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, false);
    }

    @PostMapping("page")
    public AjaxResult selectPageList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, true);
    }

    /**
     * 查询实体数据
     *
     * @param body   查询参数
     * @param isPage 是否是分页模式。分页模式，要求有pageNum/pageSize参数，并按分页格式返回
     * @return 实体数据
     */
    private AjaxResult selectEntityList(Map<String, Object> body, boolean isPage) {
        try {
            // 获得数据库中的配置
            ConfigEntity configEntity = this.getConfigEntity();

            // 提取出配置信息
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) configEntity.getConfigValue().get(DeviceDecoderVOFieldConstant.field_list);

            // 查找jar文件列表，并将配置信息和缺省值填入结果
            List<Map<String, Object>> entityList = ProtocolJarUtils.findJarConfig(dataList, false);

            return PageUtils.getPageMapList(entityList, body);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    /**
     * 插入或者更新
     *
     * @param params 参数
     * @return 操作结果
     */
    private AjaxResult insertOrUpdate(Map<String, Object> params) {
        try {
            // 提取业务参数
            String fileName = (String) params.get(DeviceDecoderVOFieldConstant.field_file_name);
            Boolean load = (Boolean) params.get(DeviceDecoderVOFieldConstant.field_load);

            // 简单校验参数
            if (MethodUtils.hasNull(fileName, load)) {
                return AjaxResult.error("参数不能为空:fileName, load");
            }

            // 获得系统配置
            ConfigEntity configEntity = this.getConfigEntity();

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) configEntity.getConfigValue().get(DeviceDecoderVOFieldConstant.field_list);

            // 修改数值
            List<Map<String, Object>> entityList = ProtocolJarUtils.findJarConfig(dataList, false);
            for (Map<String, Object> map : entityList) {
                String fileNameValue = (String) map.get(DeviceDecoderVOFieldConstant.field_file_name);

                if (fileNameValue.equals(fileName)) {
                    map.put(DeviceDecoderVOFieldConstant.field_load, load);
                }
            }
            configEntity.getConfigValue().clear();
            configEntity.getConfigValue().put(DeviceDecoderVOFieldConstant.field_list, entityList);

            // 简单验证实体的合法性
            if (configEntity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }

            // 修改数据
            this.entityManageService.updateEntity(configEntity);

            // 保存到配置文件
            ProtocolJarUtils.WriteConfigFile(entityList);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private ConfigEntity getConfigEntity() {
        String serviceName = "device-service";
        String serviceType = "system";
        String configName = "decoderConfig";

        ConfigEntity configEntity = new ConfigEntity();
        ConfigEntity existEntity = this.entityManageService.getConfigEntity(serviceName, serviceType, configName);
        if (existEntity == null) {
            configEntity.setServiceName(serviceName);
            configEntity.setServiceType(serviceType);
            configEntity.setConfigName(configName);
            configEntity.getConfigValue().put(DeviceDecoderVOFieldConstant.field_list, new ArrayList<>());
            this.entityManageService.insertEntity(configEntity);
        } else {
            configEntity.bind(existEntity);
            configEntity.getConfigValue().putAll(existEntity.getConfigValue());
        }

        return configEntity;
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntityList(@QueryParam("fileName") String fileName) {
        try {
            ConfigEntity existEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "decoderConfig");
            if (existEntity != null && existEntity.getConfigValue().containsKey("list")) {

                List<Map<String, Object>> dataList = (List<Map<String, Object>>) existEntity.getConfigValue().get("list");
                Map<String, Map<String, Object>> dataMap = ContainerUtils.buildMapByKey(dataList, DeviceDecoderVOFieldConstant.field_file_name, String.class);
                dataMap.remove(fileName);


                // 保存到数据库/redis
                ConfigEntity entity = new ConfigEntity();
                entity.bind(existEntity);

                List<Map<String, Object>> list = new ArrayList<>();
                list.addAll(dataMap.values());
                entity.getConfigValue().clear();
                entity.getConfigValue().put(DeviceDecoderVOFieldConstant.field_list, list);

                this.entityManageService.updateEntity(entity);
            }

            // 删除本地文件
            ProtocolJarUtils.deleteFile(fileName);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("process/restart")
    public AjaxResult restartProcess(@RequestBody Map<String, Object> params) {
        try {
            this.processStartService.restartProcess("device-service", "system");
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
