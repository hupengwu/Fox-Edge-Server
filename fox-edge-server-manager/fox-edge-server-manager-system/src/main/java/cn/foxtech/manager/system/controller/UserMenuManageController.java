package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.UserMenuEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.entity.constant.UserMenuVOFieldConstant;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/user/menu")
public class UserMenuManageController {
    @Autowired
    private EntityManageService entityManageService;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(UserMenuEntity.class);
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        try {
            List<BaseEntity> entityList = this.entityManageService.selectUserMenuEntityListByPage(body);
            return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult getEntity(@QueryParam("name") String name) {
        UserMenuEntity exist = this.entityManageService.getUserMenuEntity(name);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(BeanMapUtils.objectToMap(exist));
    }

    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params, true);
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params, false);
    }

    /**
     * 插入或者更新
     *
     * @param params 参数
     * @param insert 是否为插入操作
     * @return 操作结果
     */
    private AjaxResult insertOrUpdate(Map<String, Object> params, boolean insert) {
        String name = (String) params.get(UserMenuVOFieldConstant.field_name);
        List menu = (List) params.get(UserMenuVOFieldConstant.field_menu);

        // 简单校验参数
        if (MethodUtils.hasNull(name, menu)) {
            return AjaxResult.error("参数不能为空:name, menu");
        }

        UserMenuEntity entity = new UserMenuEntity();
        entity.setName(name);
        entity.getParams().addAll(menu);

        if (entity.hasNullServiceKey()) {
            return AjaxResult.error("具有null的service key！");
        }

        UserMenuEntity exist = this.entityManageService.getUserMenuEntity(name);
        if (insert) {
            if (exist != null) {
                return AjaxResult.error("实体已存在");
            }

            this.entityManageService.insertEntity(entity);
            return AjaxResult.success();
        } else {
            if (exist == null) {
                return AjaxResult.error("实体不存在");
            }

            entity.setId(exist.getId());
            this.entityManageService.updateEntity(entity);
            return AjaxResult.success();
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("name") String username) {
        UserMenuEntity exist = this.entityManageService.getUserMenuEntity(username);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        this.entityManageService.deleteEntity(exist);
        return AjaxResult.success();
    }
}
