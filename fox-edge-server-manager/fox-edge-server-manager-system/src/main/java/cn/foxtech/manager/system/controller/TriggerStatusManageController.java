package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.entity.entity.TriggerStatusEntity;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/kernel/manager/trigger/status")
public class TriggerStatusManageController {
    @Autowired
    private EntityManageService entityManageService;

    @PostMapping("entities")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        try {

            AtomicInteger limit = new AtomicInteger();

            List<BaseEntity> entityList = entityManageService.getEntityList(TriggerStatusEntity.class, (Object value) -> {
                TriggerStatusEntity entity = (TriggerStatusEntity) value;

                boolean result = true;

                if (body.containsKey("deviceName")) {
                    // 包含指定字符串
                    result &= entity.getDeviceName().indexOf((String) body.get("deviceName")) != -1;
                }
                if (body.containsKey("triggerConfigName")) {
                    // 包含指定字符串
                    result &= entity.getTriggerConfigName().indexOf((String) body.get("triggerConfigName")) != -1;
                }
                if (body.containsKey("deviceType")) {
                    result &= entity.getDeviceType().equals(body.get("deviceType"));
                }
                if (body.containsKey("manufacturer")) {
                    result &= entity.getManufacturer().equals(body.get("manufacturer"));
                }
                if (body.containsKey("triggerModelName")) {
                    // 包含指定字符串
                    result &= entity.getTriggerModelName().indexOf((String) body.get("triggerModelName")) != -1;
                }
                if (body.containsKey("triggerMethodName")) {
                    // 包含指定字符串
                    result &= entity.getTriggerMethodName().indexOf((String) body.get("triggerMethodName")) != -1;
                }
                if (body.containsKey("id")) {
                    // 等于指定ID
                    result &= entity.getId().equals(body.get("id"));
                }
                if (body.containsKey("limit")) {
                    // 最多返回的数量
                    result &= (limit.getAndIncrement()) <= (Integer) body.get("limit");
                }

                return result;
            });

            return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

}
