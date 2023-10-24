package cn.foxtech.manager.system.controller;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/console")
public class ConsoleManageController {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService consoleService;

    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        String serviceType = (String) body.get(RedisStatusConstant.field_service_type);
        String serviceName = (String) body.get(RedisStatusConstant.field_service_name);
        String level = (String) body.get("level");
        Integer pageNum = (Integer) body.get(DeviceVOFieldConstant.field_page_num);
        Integer pageSize = (Integer) body.get(DeviceVOFieldConstant.field_page_size);

        // 简单校验参数
        if (MethodUtils.hasNull(pageNum, pageSize)) {
            return AjaxResult.error("参数不能为空:pageNum, pageSize");
        }

        // 只要用户填写了过滤参数：那么就将全部记录拿出来，然后手动筛选。
        if (!MethodUtils.hasNull(serviceType) || !MethodUtils.hasNull(serviceName) || !MethodUtils.hasNull(level)) {
            List<Map<String, Object>> range = this.consoleService.range();
            List list = new ArrayList<>();
            for (Map<String, Object> object : range) {
                if (!MethodUtils.hasEmpty(serviceType) && !this.like(object, RedisStatusConstant.field_service_type, serviceType)) {
                    continue;
                }
                if (!MethodUtils.hasEmpty(serviceName) && !this.like(object, RedisStatusConstant.field_service_name, serviceName)) {
                    continue;
                }
                if (!MethodUtils.hasEmpty(level) && !this.like(object, "level", level)) {
                    continue;
                }

                Object value = object.get("value");
                if (value != null && !(value instanceof String)) {
                    object.put("value", JsonUtils.buildJsonWithoutException(value));
                }

                list.add(object);
            }

            Map<String, Object> data = PageUtils.getPageList(list, pageNum, pageSize);
            return AjaxResult.success(data);
        } else {
            // 用户没有填写过滤条件，那么直接在redis分页取数据
            Integer pageStartId = pageSize * (pageNum - 1);
            Integer pageEndId = pageSize * pageNum;

            Long total = this.consoleService.size();
            List list = this.consoleService.range(pageStartId.longValue(), pageEndId.longValue());

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            return AjaxResult.success(data);
        }
    }

    private boolean like(Map<String, Object> map, String key, String value) {
        if (value == null) {
            return false;
        }

        Object data = map.get(key);
        if (data == null) {
            return false;
        }

        if (data instanceof String) {
            return ((String) data).contains(value);
        }

        return false;
    }
}
