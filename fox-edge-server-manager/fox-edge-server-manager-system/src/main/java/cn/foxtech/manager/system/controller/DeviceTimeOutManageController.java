package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.constant.DeviceStatusVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceTimeOutEntity;
import cn.foxtech.common.entity.service.device.DeviceEntityService;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.ExtendUtils;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/kernel/manager/device/timeout")
public class DeviceTimeOutManageController {
    @Autowired
    protected DeviceEntityService deviceEntityService;
    @Autowired
    private EntityManageService entityManageService;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceTimeOutEntity.class);
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
            // 获得Key信息
            List<BaseEntity> deviceEntityList = this.deviceEntityService.selectEntityList();
            Map<Long, BaseEntity> map = ContainerUtils.buildMapByKey(deviceEntityList, DeviceEntity::getId);

            List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceTimeOutEntity.class, (Object value) -> {
                DeviceTimeOutEntity entity = (DeviceTimeOutEntity) value;

                boolean result = true;

                DeviceEntity deviceEntity = (DeviceEntity) map.get(entity.getId());
                if (deviceEntity == null) {
                    return false;
                }

                if (body.containsKey(DeviceStatusVOFieldConstant.field_device_name)) {
                    result = deviceEntity.getDeviceName().contains((String) body.get(DeviceStatusVOFieldConstant.field_device_name));
                }
                if (body.containsKey(DeviceStatusVOFieldConstant.field_device_type)) {
                    result &= deviceEntity.getDeviceType().equals(body.get(DeviceStatusVOFieldConstant.field_device_type));
                }
                if (body.containsKey(DeviceStatusVOFieldConstant.field_failed_count)) {
                    result &= entity.getCommFailedCount() >= ((Integer) body.get(DeviceStatusVOFieldConstant.field_failed_count));
                }
                if (body.containsKey(DeviceStatusVOFieldConstant.field_failed_time)) {
                    result &= entity.getCommFailedTime() >= ((Integer) body.get(DeviceStatusVOFieldConstant.field_failed_time));
                }
                if (body.containsKey(DeviceStatusVOFieldConstant.field_success_time)) {
                    result &= entity.getCommSuccessTime() >= ((Integer) body.get(DeviceStatusVOFieldConstant.field_success_time));
                }

                return result;
            });

            Set<String> extend = new HashSet<>();
            extend.add(DeviceVOFieldConstant.field_device_name);
            extend.add(DeviceVOFieldConstant.field_device_type);
            extend.add(DeviceVOFieldConstant.field_channel_name);
            extend.add(DeviceVOFieldConstant.field_channel_type);

            // 获得分页数据
            if (isPage) {
                AjaxResult ajaxResult = PageUtils.getPageList(entityList, body);
                Map<String, Object> data = (Map<String, Object>) ajaxResult.get("data");
                ExtendUtils.extend((List<Map<String, Object>>) data.get("list"), deviceEntityList, "id", extend);
                return ajaxResult;
            } else {
                AjaxResult ajaxResult = AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
                ExtendUtils.extend((List<Map<String, Object>>) ajaxResult.get("data"), deviceEntityList, "id", extend);
                return ajaxResult;
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        DeviceTimeOutEntity exist = this.entityManageService.getEntity(id, DeviceTimeOutEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
    }
}
