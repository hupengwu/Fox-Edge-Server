package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.constant.DeviceHistoryVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceHistoryEntity;
import cn.foxtech.common.entity.entity.DeviceHistoryPo;
import cn.foxtech.common.entity.service.device.DeviceEntityService;
import cn.foxtech.common.entity.service.devicehistory.DeviceHistoryEntityMaker;
import cn.foxtech.common.entity.service.devicehistory.DeviceHistoryPoMapper;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.ExtendUtils;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.pair.Pair;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/kernel/manager/device/history")
public class DeviceHistoryManageController {
    @Autowired
    protected DeviceEntityService deviceEntityService;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private DeviceHistoryPoMapper mapper;


    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        String deviceName = (String) body.get(DeviceHistoryVOFieldConstant.field_device_name);
        String objectName = (String) body.get(DeviceHistoryVOFieldConstant.field_object_name);
        Integer pageNum = (Integer) body.get(DeviceHistoryVOFieldConstant.field_page_num);
        Integer pageSize = (Integer) body.get(DeviceHistoryVOFieldConstant.field_page_size);

        // 简单校验参数
        if (MethodUtils.hasNull(pageNum, pageSize)) {
            return AjaxResult.error("参数不能为空:pageNum, pageSize");
        }

        StringBuilder sb = new StringBuilder();
        if (deviceName != null) {
            DeviceEntity deviceEntity = this.entityManageService.getDeviceEntity(deviceName);
            if (deviceEntity != null) {
                sb.append(" (device_id = ").append(deviceEntity.getId()).append(") AND");
            }
        }
        if (objectName != null) {
            sb.append(" (object_name = '").append(objectName).append("') AND");
        }
        String filter = sb.toString();
        if (!filter.isEmpty()) {
            filter = filter.substring(0, filter.length() - "AND".length());
        }

        AjaxResult ajaxResult = this.selectEntityListPage(filter, "ASC", pageNum, pageSize);

        // 添加扩展信息
        Map<String, Object> data = (Map<String, Object>) ajaxResult.get("data");
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");
        List<Long> idList = new ArrayList<>();
        for (Map<String, Object> row : list) {
            idList.add((Long) row.get("deviceId"));
        }

        // 根据ID查询记录
        List<BaseEntity> deviceEntityList = this.deviceEntityService.selectListBatchIds(idList);

        Set<String> extend = new HashSet<>();
        List<Pair<String, String>> keypairs = new ArrayList<>();
        keypairs.add(new Pair<>(DeviceHistoryVOFieldConstant.field_device_id, DeviceVOFieldConstant.field_id));
        extend.add(DeviceVOFieldConstant.field_device_name);
        extend.add(DeviceVOFieldConstant.field_manufacturer);
        extend.add(DeviceVOFieldConstant.field_device_type);
        extend.add(DeviceVOFieldConstant.field_channel_name);
        extend.add(DeviceVOFieldConstant.field_channel_type);
        ExtendUtils.extend((List<Map<String, Object>>) data.get("list"), deviceEntityList, keypairs, extend);


        return ajaxResult;
    }

    public AjaxResult selectEntityListPage(String filter, String order, long pageNmu, long pageSize) {
        try {
            // 查询总数
            String selectCount = PageUtils.makeSelectCountSQL("tb_device_history", filter);
            Integer total = this.mapper.executeSelectCount(selectCount);

            // 分页查询数据
            String selectPage = PageUtils.makeSelectSQLPage("tb_device_history", filter, order, total, pageNmu, pageSize);
            List<DeviceHistoryPo> poList = this.mapper.executeSelectData(selectPage);

            List<DeviceHistoryEntity> entityList = DeviceHistoryEntityMaker.makePoList2EntityList(poList);

            Map<String, Object> data = new HashMap<>();
            data.put("list", EntityVOBuilder.buildVOList(entityList));
            data.put("total", total);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
