package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.service.RepoLocalApplicationService;
import cn.foxtech.kernel.system.repository.service.RepoLocalCompService;
import cn.foxtech.kernel.system.repository.service.RepoLocalCsvFileService;
import cn.foxtech.kernel.system.repository.service.RepoLocalJarFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 组件列表管理：包括应用服务、静态解码、动态解码、文件模板的组件级别的管理
 */
@RestController
@RequestMapping("/kernel/manager/repository/local/comp-list")
public class RepoLocalCompListController {
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 仓库服务
     */
    @Autowired
    private RepoLocalCompService compService;

    @Autowired
    private RepoLocalApplicationService appServerService;

    @Autowired
    private RepoLocalCsvFileService fileService;

    @Autowired
    private RepoLocalJarFileInfoService jarFileInfoService;


    @PostMapping("page")
    public Map<String, Object> selectCompPage(@RequestBody Map<String, Object> body) {
        try {
            String compRepo = (String) body.get(RepoCompVOFieldConstant.field_comp_repo);
            String compType = (String) body.get(RepoCompVOFieldConstant.field_comp_type);

            if (MethodUtils.hasEmpty(compRepo, compType)) {
                throw new ServiceException("参数不能为空: compRepo, compType");
            }
            // 查询数据
            List<BaseEntity> entityList = this.compService.getCompEntityList(body);

            List<Map<String, Object>> mapList = new ArrayList<>();

            // 服务场景：进行登记排序
            if (compType.equals(RepoCompVOFieldConstant.value_comp_type_app_service)) {
                entityList = this.appServerService.sort(entityList);

                mapList = BeanMapUtils.objectToMap(entityList);
            } else if (compType.equals(RepoCompVOFieldConstant.value_comp_type_file_template)) {
                mapList = this.fileService.extendCompFileCount(entityList);
            } else if (compType.equals(RepoCompVOFieldConstant.value_comp_type_jar_decoder)) {
                mapList = this.jarFileInfoService.extendCompJarInfo(entityList);
            } else {
                mapList = BeanMapUtils.objectToMap(entityList);
            }

            // 分页查询
            return PageUtils.getPageMapList(mapList, body);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    /**
     * 插入或者更新
     *
     * @param params 参数
     * @return 操作结果
     */
    private AjaxResult insertOrUpdate(Map<String, Object> params) {
        try {
            // 构造作为参数的实体
            RepoCompEntity entity = this.compService.buildCompEntity(params);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }

            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                RepoCompEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), RepoCompEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                RepoCompEntity exist = this.entityManageService.getEntity(id, RepoCompEntity.class);
                if (exist == null) {
                    return AjaxResult.error("实体不存在");
                }

                // 修改数据
                entity.setId(id);
                this.entityManageService.updateEntity(entity);
                return AjaxResult.success();
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        try {
            // 删除对象
            this.compService.deleteCompEntity(id);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("upload")
    public AjaxResult uploadEntity(@RequestBody Map<String, Object> params) {
        try {
            Integer id = (Integer) params.get(RepoCompVOFieldConstant.field_id);
            String commitKey = (String) params.get(RepoCompVOFieldConstant.field_commit_key);
            String description = (String) params.get(RepoCompVOFieldConstant.field_description);
            if (MethodUtils.hasEmpty(id, commitKey, description)) {
                throw new ServiceException("参数不能为空: id, commitKey, description");
            }


            // 上传组件
            Map<String, Object> result = this.compService.uploadEntity(Long.parseLong(id.toString()), commitKey, description);

            return JsonUtils.buildObject(result, AjaxResult.class);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("sync")
    public AjaxResult syncCloud(@RequestBody Map<String, Object> params) {
        try {
            Integer id = (Integer) params.get(RepoCompVOFieldConstant.field_id);
            if (MethodUtils.hasNull(id)) {
                throw new ServiceException("参数不能为空: id");
            }

            // 上传组件
            Map<String, Object> result = this.compService.syncEntity(Long.parseLong(id.toString()));

            return JsonUtils.buildObject(result, AjaxResult.class);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
