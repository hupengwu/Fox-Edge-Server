package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.entity.constant.TriggerVOFieldConstant;
import cn.foxtech.common.entity.entity.TriggerEntity;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

/**
 * operate的数据是由deviceService服务启动阶段，自动扫描第三方jar生成的，不需要手动维护
 */
@RestController
@RequestMapping("/kernel/manager/device/trigger")
public class TriggerManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(TriggerEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(TriggerEntity.class, (Object value) -> {
                TriggerEntity entity = (TriggerEntity) value;

                boolean result = true;


                if (body.containsKey(TriggerVOFieldConstant.field_model_name)) {
                    result = entity.getModelName().equals(body.get(TriggerVOFieldConstant.field_model_name));
                }
                if (body.containsKey(TriggerVOFieldConstant.field_method_name)) {
                    result &= entity.getMethodName().equals(body.get(TriggerVOFieldConstant.field_method_name));
                }
                if (body.containsKey(TriggerVOFieldConstant.field_manufacturer)) {
                    result &= entity.getManufacturer().equals(body.get(TriggerVOFieldConstant.field_manufacturer));
                }

                return result;
            });


            // 获得分页数据
            if (isPage) {
                return PageUtils.getPageList(entityList, body);
            } else {
                return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        TriggerEntity exist = this.entityManageService.getEntity(id, TriggerEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
    }
}
