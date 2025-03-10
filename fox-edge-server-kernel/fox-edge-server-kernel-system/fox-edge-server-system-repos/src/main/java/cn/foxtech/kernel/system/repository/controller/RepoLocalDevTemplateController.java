/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.entity.constant.DeviceTemplateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceTemplateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.service.RepoLocalCompService;
import cn.foxtech.kernel.system.repository.service.RepoLocalDevTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

/**
 * 操作方法列表管理：包括JSP方法和JAR方法
 */
@RestController
@RequestMapping("/repository/local/dev-template-list")
public class RepoLocalDevTemplateController {
    /**
     * 仓库服务
     */
    @Autowired
    private RepoLocalCompService componentService;

    @Autowired
    private RepoLocalDevTemplateService templateService;

    @PostMapping("page")
    public Map<String, Object> selectEntityPage(@RequestBody Map<String, Object> body) {
        try {
            Integer id = (Integer) body.get(RepoCompVOFieldConstant.field_id);
            Integer pageNum = (Integer) body.get(RepoCompVOFieldConstant.field_page_num);
            Integer pageSize = (Integer) body.get(RepoCompVOFieldConstant.field_page_size);
            String templateType = (String) body.get(DeviceTemplateVOFieldConstant.field_template_type);


            // 简单验证
            if (MethodUtils.hasEmpty(id, pageNum, pageSize, templateType)) {
                throw new ServiceException("参数不能为空: id, pageNum, pageSize, templateType");
            }

            // 获得组件信息
            RepoCompEntity compEntity = this.componentService.getRepoCompEntity(Long.parseLong(id.toString()));
            if (compEntity == null) {
                throw new ServiceException("找不到对应的组件：" + id);
            }

            // 查询数据
            List<BaseEntity> entityList = this.templateService.getDevTemplateEntityList(compEntity, templateType);

            // 分页查询
            return AjaxResult.success(PageUtils.getPageList(entityList, pageNum, pageSize));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("entities")
    public Map<String, Object> selectEntityList(@RequestBody Map<String, Object> body) {
        try {
            List<BaseEntity> entityList = this.templateService.selectEntityList(body);

            // 分页查询
            return AjaxResult.success(entityList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        DeviceTemplateEntity exist = this.templateService.queryEntity(id);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
    }

    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        try {
            params.remove("id");
            this.templateService.insertOrUpdate(params);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        try {
            this.templateService.insertOrUpdate(params);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        try {
            this.templateService.deleteEntity(id);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entities")
    public AjaxResult deleteEntityList(@QueryParam("ids") String ids) {
        String[] idList = ids.split(",");

        for (String id : idList) {
            if (id == null || id.isEmpty()) {
                continue;
            }

            this.templateService.deleteEntity(Long.parseLong(id));
        }

        return AjaxResult.success();
    }

    @PostMapping("option")
    public AjaxResult selectOptionList(@RequestBody Map<String, Object> body) {
        try {
            List<Map<String, Object>> resultList = this.templateService.selectOptionList(body);

            return AjaxResult.success(resultList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("group")
    public AjaxResult selectGroupList(@QueryParam("id") Long id) {
        try {
            // 获得组件信息
            RepoCompEntity compEntity = this.componentService.getRepoCompEntity(Long.parseLong(id.toString()));
            if (compEntity == null) {
                throw new ServiceException("找不到对应的组件：" + id);
            }

            List<Map<String, Object>> resultList = this.templateService.selectGroupList(compEntity.getCompParam());

            return AjaxResult.success(resultList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
