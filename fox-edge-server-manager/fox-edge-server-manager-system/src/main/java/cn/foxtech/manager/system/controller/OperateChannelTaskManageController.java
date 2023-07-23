package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateChannelTaskEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.entity.constant.OperateChannelTaskVOFieldConstant;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device/operate/task/channel")
public class OperateChannelTaskManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateChannelTaskEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateChannelTaskEntity.class, (Object value) -> {
                OperateChannelTaskEntity entity = (OperateChannelTaskEntity) value;

                boolean result = true;

                if (body.containsKey(OperateChannelTaskVOFieldConstant.field_task_name)) {
                    result = entity.getTaskName().contains((String) body.get(OperateChannelTaskVOFieldConstant.field_task_name));
                }
                if (body.containsKey(OperateChannelTaskVOFieldConstant.field_channel_name)) {
                    result &= entity.getChannelName().contains((String) body.get(OperateChannelTaskVOFieldConstant.field_channel_name));
                }
                if (body.containsKey(OperateChannelTaskVOFieldConstant.field_channel_type)) {
                    result &= entity.getChannelType().equals(body.get(OperateChannelTaskVOFieldConstant.field_channel_type));
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
        OperateChannelTaskEntity exist = this.entityManageService.getEntity(id, OperateChannelTaskEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
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
            // 提取业务参数
            String taskName = (String) params.get(OperateChannelTaskVOFieldConstant.field_task_name);
            String channelName = (String) params.get(OperateChannelTaskVOFieldConstant.field_channel_name);
            String channelType = (String) params.get(OperateChannelTaskVOFieldConstant.field_channel_type);
            String sendMode = (String) params.get(OperateChannelTaskVOFieldConstant.field_send_mode);
            Integer timeout = (Integer) params.get(OperateChannelTaskVOFieldConstant.field_timeout);
            Map<String, Object> taskParam = (Map<String, Object>) params.get(OperateChannelTaskVOFieldConstant.field_task_param);

            // 简单校验参数
            if (MethodUtils.hasNull(taskName, channelName, channelType, taskParam, sendMode, timeout)) {
                return AjaxResult.error("参数不能为空:taskName, channelName, channelType, taskParam, sendMode, timeout");
            }

            // 构造作为参数的实体
            OperateChannelTaskEntity entity = new OperateChannelTaskEntity();
            entity.setTaskName(taskName);
            entity.setChannelName(channelName);
            entity.setChannelType(channelType);
            entity.setSendMode(sendMode);
            entity.setTimeout(timeout);
            entity.setTaskParam(taskParam);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }

            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                OperateChannelTaskEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), OperateChannelTaskEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                OperateChannelTaskEntity exist = this.entityManageService.getEntity(id, OperateChannelTaskEntity.class);
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
        OperateChannelTaskEntity exist = this.entityManageService.getEntity(id, OperateChannelTaskEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        this.entityManageService.deleteEntity(exist);
        return AjaxResult.success();
    }

    @DeleteMapping("entities")
    public AjaxResult deleteEntityList(@QueryParam("ids") String ids) {
        String[] idList = ids.split(",");

        for (String id : idList) {
            if (id == null || id.isEmpty()) {
                continue;
            }

            this.entityManageService.deleteEntity(Long.parseLong(id), OperateChannelTaskEntity.class);
        }

        return AjaxResult.success();
    }
}
