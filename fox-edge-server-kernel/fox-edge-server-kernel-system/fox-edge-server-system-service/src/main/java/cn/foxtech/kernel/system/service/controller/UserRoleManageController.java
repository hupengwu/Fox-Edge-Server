package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.constant.UserRoleVOFieldConstant;
import cn.foxtech.common.entity.entity.UserRoleEntity;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/user/role")
public class UserRoleManageController {
    @Autowired
    private EntityManageService entityManageService;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(UserRoleEntity.class);
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        try {
            List<BaseEntity> entityList = this.entityManageService.selectUserRoleEntityListByPage(body);
            return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult getEntity(@QueryParam("name") String name) {
        UserRoleEntity exist = this.entityManageService.getUserRoleEntity(name);
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
        String name = (String) params.get(UserRoleVOFieldConstant.field_name);
        List role = (List) params.get(UserRoleVOFieldConstant.field_role);

        // 简单校验参数
        if (MethodUtils.hasNull(name, role)) {
            return AjaxResult.error("参数不能为空:name, role");
        }

        UserRoleEntity entity = new UserRoleEntity();
        entity.setName(name);
        entity.getParams().addAll(role);

        if (entity.hasNullServiceKey()) {
            return AjaxResult.error("具有null的service key！");
        }

        UserRoleEntity exist = this.entityManageService.getUserRoleEntity(name);
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
        UserRoleEntity exist = this.entityManageService.getUserRoleEntity(username);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        this.entityManageService.deleteEntity(exist);
        return AjaxResult.success();
    }
}
