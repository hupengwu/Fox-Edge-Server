package cn.foxtech.period.service.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.ExtendUtils;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.pair.Pair;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.period.service.entity.PeriodRecordEntity;
import cn.foxtech.period.service.entity.PeriodTaskEntity;
import cn.foxtech.period.service.mapper.periodrecord.PeriodRecordEntityMapper;
import cn.foxtech.period.service.service.EntityManageService;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.constant.PeriodRecordVOFieldConstant;
import cn.foxtech.common.entity.constant.PeriodTaskVOFieldConstant;
import cn.foxtech.common.entity.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/service/period/record/object")
public class PeriodRecordController {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private PeriodRecordEntityMapper mapper;

    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        String taskName = (String) body.get(PeriodRecordVOFieldConstant.field_task_name);
        String recordBatch = (String) body.get(PeriodRecordVOFieldConstant.field_record_batch);
        String deviceName = (String) body.get(PeriodRecordVOFieldConstant.field_device_name);
        String objectName = (String) body.get(PeriodRecordVOFieldConstant.field_object_name);
        Integer pageNum = (Integer) body.get(PeriodRecordVOFieldConstant.field_page_num);
        Integer pageSize = (Integer) body.get(PeriodRecordVOFieldConstant.field_page_size);

        // 简单校验参数
        if (MethodUtils.hasNull(pageNum, pageSize)) {
            return PageUtils.ajaxResultEmpty();
        }

        // 构造过滤条件
        StringBuilder sb = new StringBuilder();
        if (!MethodUtils.hasEmpty(taskName)) {
            PeriodTaskEntity entity = new PeriodTaskEntity();
            entity.setTaskName(taskName);
            PeriodTaskEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), PeriodTaskEntity.class);
            if (exist != null) {
                sb.append(" (task_id = ").append(exist.getId()).append(") AND");
            }
        }
        if (!MethodUtils.hasEmpty(deviceName)) {
            DeviceEntity deviceEntity = this.entityManageService.getDeviceEntity(deviceName);
            if (deviceEntity != null) {
                sb.append(" (device_id = ").append(deviceEntity.getId()).append(") AND");
            }
        }
        if (!MethodUtils.hasEmpty(objectName)) {
            sb.append(" (object_name = '").append(objectName).append("') AND");
        }
        if (!MethodUtils.hasEmpty(recordBatch)) {
            sb.append(" (record_batch = '").append(recordBatch).append("') AND");
        }
        String filter = sb.toString();
        if (!filter.isEmpty()) {
            filter = filter.substring(0, filter.length() - "AND".length());
        }

        AjaxResult ajaxResult = this.selectEntityListPage(filter, "ASC", pageNum, pageSize);

        // 添加扩展信息
        Map<String, Object> data = (Map<String, Object>) ajaxResult.get("data");
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");
        this.extend(list);

        return ajaxResult;
    }

    private void extend(List<Map<String, Object>> list) {
        List<BaseEntity> deviceEntityList = new ArrayList<>();
        for (Map<String, Object> row : list) {
            DeviceEntity deviceEntity = this.entityManageService.getEntity((Long) row.get("deviceId"), DeviceEntity.class);
            if (deviceEntity != null) {
                deviceEntityList.add(deviceEntity);
            }
        }

        Set<String> extend = new HashSet<>();
        List<Pair<String, String>> keypairs = new ArrayList<>();
        keypairs.add(new Pair<>(PeriodRecordVOFieldConstant.field_device_id, DeviceVOFieldConstant.field_id));
        extend.add(DeviceVOFieldConstant.field_device_name);
        extend.add(DeviceVOFieldConstant.field_manufacturer);
        extend.add(DeviceVOFieldConstant.field_device_type);
        extend.add(DeviceVOFieldConstant.field_channel_name);
        extend.add(DeviceVOFieldConstant.field_channel_type);
        ExtendUtils.extend(list, deviceEntityList, keypairs, extend);

        List<BaseEntity> taskEntityList = new ArrayList<>();
        for (Map<String, Object> row : list) {
            PeriodTaskEntity periodTaskEntity = this.entityManageService.getEntity((Long) row.get("taskId"), PeriodTaskEntity.class);
            if (periodTaskEntity != null) {
                taskEntityList.add(periodTaskEntity);
            }
        }

        extend = new HashSet<>();
        keypairs = new ArrayList<>();
        keypairs.add(new Pair<>(PeriodRecordVOFieldConstant.field_task_id, PeriodTaskVOFieldConstant.field_id));
        extend.add(PeriodTaskVOFieldConstant.field_task_name);
        ExtendUtils.extend(list, taskEntityList, keypairs, extend);
    }

    public AjaxResult selectEntityListPage(String filter, String order, long pageNmu, long pageSize) {
        try {
            // 查询总数
            String selectCount = PageUtils.makeSelectCountSQL("tb_period_record", filter);
            Integer total = this.mapper.executeSelectCount(selectCount);

            // 分页查询数据
            String selectPage = PageUtils.makeSelectSQLPage("tb_period_record", filter, order, total, pageNmu, pageSize);
            List<PeriodRecordEntity> poList = this.mapper.executeSelectData(selectPage);
            for (PeriodRecordEntity po : poList) {
                DeviceObjectValue deviceObjectValue = JsonUtils.buildObjectWithoutException((String) po.getObjectValue(), DeviceObjectValue.class);
                po.setObjectValue(deviceObjectValue);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", EntityVOBuilder.buildVOList(poList));
            data.put("total", total);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
