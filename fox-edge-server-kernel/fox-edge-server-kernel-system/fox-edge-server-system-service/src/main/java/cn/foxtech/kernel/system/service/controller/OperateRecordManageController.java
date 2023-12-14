package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.entity.constant.OperateRecordVOFieldConstant;
import cn.foxtech.common.entity.entity.OperateRecordEntity;
import cn.foxtech.common.entity.entity.OperateRecordPo;
import cn.foxtech.common.entity.service.operaterecord.OperateRecordEntityMapper;
import cn.foxtech.common.entity.service.operaterecord.OperateRecordMaker;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/kernel/manager/device/operate/record")
public class OperateRecordManageController {
    @Autowired
    private OperateRecordEntityMapper mapper;


    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        String deviceName = (String) body.get(OperateRecordVOFieldConstant.field_device_name);
        String deviceType = (String) body.get(OperateRecordVOFieldConstant.field_device_type);
        String manufacturer = (String) body.get(OperateRecordVOFieldConstant.field_manufacturer);
        String recordName = (String) body.get(OperateRecordVOFieldConstant.field_record_name);
        String clientModel = (String) body.get(OperateRecordVOFieldConstant.field_client_model);
        Integer pageNum = (Integer) body.get(OperateRecordVOFieldConstant.field_page_num);
        Integer pageSize = (Integer) body.get(OperateRecordVOFieldConstant.field_page_size);

        // 简单校验参数
        if (MethodUtils.hasNull(pageNum, pageSize)) {
            return AjaxResult.error("参数不能为空:pageNum, pageSize");
        }

        StringBuilder sb = new StringBuilder();
        if (deviceName != null) {
            sb.append(" (device_name = '").append(deviceName).append("') AND");
        }
        if (deviceType != null) {
            sb.append(" (device_type = '").append(deviceType).append("') AND");
        }
        if (manufacturer != null) {
            sb.append(" (manufacturer = '").append(manufacturer).append("') AND");
        }
        if (recordName != null) {
            sb.append(" (record_name = '").append(recordName).append("') AND");
        }
        if (clientModel != null) {
            sb.append(" (client_model = '").append(clientModel).append("') AND");
        }
        String filter = sb.toString();
        if (!filter.isEmpty()) {
            filter = filter.substring(0, filter.length() - "AND".length());
        }

        return this.selectEntityListPage(filter, "DESC", pageNum, pageSize);
    }

    public AjaxResult selectEntityListPage(String filter, String order, long pageNmu, long pageSize) {
        try {
            // 查询总数
            String selectCount = PageUtils.makeSelectCountSQL("tb_operate_record", filter);
            Integer total = this.mapper.executeSelectCount(selectCount);

            // 分页查询数据
            String selectPage = PageUtils.makeSelectSQLPage("tb_operate_record", filter, order, total, pageNmu, pageSize);
            List<OperateRecordPo> poList = this.mapper.executeSelectData(selectPage);

            List<OperateRecordEntity> entityList = OperateRecordMaker.makePoList2EntityList(poList);

            Map<String, Object> data = new HashMap<>();
            data.put("list", EntityVOBuilder.buildVOList(entityList));
            data.put("total", total);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
