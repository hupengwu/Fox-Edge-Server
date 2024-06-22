package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.entity.constant.ChannelVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.ChannelStatusEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据的来源：channel数据是由channel服务启动阶段，根据自己的配置文件保存在内存中的。
 * 数据库记录的生成：它们的数据由system进程向各channel服务进程收集，并保存到数据库中。
 * 数据库记录的消费：device服务进程和调度服务进程，从数据库中读取并消费
 */
@RestController
@RequestMapping("/kernel/manager/channel/status")
public class ChannelStatusManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(ChannelEntity.class);
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
            List<BaseEntity> resultList = this.entityManageService.getEntityList(ChannelStatusEntity.class, (Object entity) -> {
                ChannelStatusEntity channelEntity = (ChannelStatusEntity) entity;

                boolean result = true;

                if (body.containsKey(ChannelVOFieldConstant.field_channel_name)) {
                    result &= channelEntity.getChannelName().contains((String) body.get(ChannelVOFieldConstant.field_channel_name));
                }
                if (body.containsKey(ChannelVOFieldConstant.field_channel_type)) {
                    result &= channelEntity.getChannelType().equals(body.get(ChannelVOFieldConstant.field_channel_type));
                }

                return result;
            });
            // 获得分页数据
            if (isPage) {
                return PageUtils.getPageList(resultList, body);
            } else {
                return AjaxResult.success(EntityVOBuilder.buildVOList(resultList));
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
