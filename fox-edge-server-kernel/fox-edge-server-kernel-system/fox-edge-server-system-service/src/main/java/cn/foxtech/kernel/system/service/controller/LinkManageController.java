package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.constant.LinkVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.LinkEntity;
import cn.foxtech.common.entity.service.foxsql.FoxSqlService;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.link.domain.LinkRequestVO;
import cn.foxtech.link.domain.LinkRespondVO;
import cn.foxtech.link.domain.LinkVOConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.*;

/**
 * 数据的来源：link数据是由link服务启动阶段，根据自己的配置文件保存在内存中的。
 * 数据库记录的生成：它们的数据由system进程向各link服务进程收集，并保存到数据库中。
 * 数据库记录的消费：device服务进程和调度服务进程，从数据库中读取并消费
 */
@RestController
@RequestMapping("/kernel/manager/link")
public class LinkManageController {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ServiceStatus serviceStatus;

    @Autowired
    private FoxSqlService foxSqlService;

    @Autowired
    private RedisTopicPublisher publisher;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(LinkEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(LinkEntity.class, (Object value) -> {
                LinkEntity entity = (LinkEntity) value;

                boolean result = true;

                if (body.containsKey(LinkVOFieldConstant.field_link_name)) {
                    result = entity.getLinkName().contains((String) body.get(LinkVOFieldConstant.field_link_name));
                }
                if (body.containsKey(LinkVOFieldConstant.field_link_type)) {
                    result &= entity.getLinkType().equals(body.get(LinkVOFieldConstant.field_link_type));
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

    @GetMapping("types")
    public AjaxResult queryLinkType() {
        Set<String> linkTypes = new HashSet<>();

        // 正在运行的link类型的进程
        List<Map<String, Object>> processList = this.serviceStatus.getDataList(60 * 1000);
        for (Map<String, Object> process : processList) {
            String modelType = (String) process.get(RedisStatusConstant.field_model_type);
            if (!RedisStatusConstant.value_model_type_link.equals(modelType)) {
                continue;
            }

            String modelName = (String) process.get(RedisStatusConstant.field_model_name);
            if (MethodUtils.hasEmpty(modelName)) {
                continue;
            }

            linkTypes.add(modelName);
        }

        // 到数据库中查找同类数据：历史上启动过，但现在停止运行的link进程
        List<Map<String, Object>> optionList = this.foxSqlService.selectOptionList("tb_link", "link_type", false);
        for (Map<String, Object> process : optionList) {
            String linkType = (String) process.get("link_type");
            if (linkType == null || linkType.isEmpty()) {
                continue;
            }

            linkTypes.add(linkType);
        }

        return AjaxResult.success(linkTypes);
    }

    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        if (id == null) {
            return AjaxResult.error("输入的id为null!");
        }
        LinkEntity exist = this.entityManageService.getEntity(id, LinkEntity.class);
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
            String linkType = (String) params.get(LinkVOFieldConstant.field_link_type);
            String linkName = (String) params.get(LinkVOFieldConstant.field_link_name);
            Map<String, Object> linkParam = (Map<String, Object>) params.get(LinkVOFieldConstant.field_link_param);
            Map<String, Object> extendParam = (Map<String, Object>) params.get(LinkVOFieldConstant.field_extend_param);

            // 简单校验参数
            if (MethodUtils.hasNull(linkType, linkName, linkParam, extendParam)) {
                return AjaxResult.error("参数不能为空: linkType, linkName, linkParam, extendParam");
            }

            // 构造作为参数的实体
            LinkEntity entity = new LinkEntity();
            entity.setLinkType(linkType);
            entity.setLinkName(linkName);
            entity.setLinkParam(linkParam);
            entity.setExtendParam(extendParam);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (params.get("id") == null) {
                LinkEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), LinkEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                LinkEntity exist = this.entityManageService.getEntity(id, LinkEntity.class);
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
        LinkEntity exist = this.entityManageService.getLinkEntity(id);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), LinkEntity.class);
        }

        return AjaxResult.success();
    }

    @PostMapping("status")
    public AjaxResult queryLinkStatus(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        List<Map<String, Object>> linkKeyList = (List<Map<String, Object>>) body.get(LinkVOFieldConstant.field_link_keys);

        // 简单校验参数
        if (MethodUtils.hasEmpty(linkKeyList)) {
            return AjaxResult.error("参数不能为空:linkKey");
        }

        try {
            // 按linkType进行分类组织
            Map<String, Set<String>> type2names = new HashMap<>();
            for (Map<String, Object> linkKey : linkKeyList) {
                String linkType = (String) linkKey.get(LinkVOFieldConstant.field_link_type);
                String linkName = (String) linkKey.get(LinkVOFieldConstant.field_link_name);
                if (MethodUtils.hasEmpty(linkType, linkName)) {
                    continue;
                }

                Set<String> linkNames = type2names.computeIfAbsent(linkType, k -> new HashSet<>());
                linkNames.add(linkName);
            }

            List<Map<String, Object>> result = new ArrayList<>();

            // 按linkType分批发送请求到各自的link服务上进行查询
            for (String linkType : type2names.keySet()) {
                Set<String> linkNames = type2names.get(linkType);

                // 按linkType，分批查询数据
                LinkRespondVO respondVO = this.queryLinkStatus(linkType, linkNames);
                if (!HttpStatus.SUCCESS.equals(respondVO.getCode())) {
                    continue;
                }

                Map<String, Object> linkStatusMap = (Map<String, Object>) respondVO.getRecv();
                if (MethodUtils.hasEmpty(linkStatusMap)) {
                    continue;
                }

                for (String linkName : linkNames) {
                    Map<String, Object> status = (Map<String, Object>) linkStatusMap.getOrDefault(linkName, new HashMap<>());
                    status.put(LinkVOConstant.value_link_name, linkName);
                    result.add(status);
                }
            }


            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private LinkRespondVO queryLinkStatus(String linkType, Collection<String> linkNameList) throws IOException, InterruptedException {
        Map<String, Object> param = new HashMap<>();
        param.put(LinkVOConstant.filed_operate, LinkVOConstant.value_operate_get_status);
        param.put(LinkVOConstant.filed_param, linkNameList);

        LinkRequestVO requestVO = new LinkRequestVO();
        requestVO.setUuid(UUID.randomUUID().toString());
        requestVO.setType(linkType);
        requestVO.setMode(LinkRequestVO.MODE_MANAGE);
        requestVO.setSend(param);

        // 重置信号
        SyncFlagObjectMap.inst().reset(requestVO.getUuid());

        // 发送数据
        this.publisher.sendMessage(RedisTopicConstant.topic_link_request + linkType, requestVO);

        // 等待消息的到达：根据动态key
        LinkRespondVO respond = (LinkRespondVO) SyncFlagObjectMap.inst().waitDynamic(requestVO.getUuid(), 2 * 1000);
        if (respond == null) {
            return LinkRespondVO.error("服务响应超时！");
        }

        return respond;
    }
}
