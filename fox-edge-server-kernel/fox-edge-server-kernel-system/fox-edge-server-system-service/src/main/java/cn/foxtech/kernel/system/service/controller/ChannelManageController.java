package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.domain.ChannelVOConstant;
import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.constant.ChannelVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.service.foxsql.FoxSqlService;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.*;

/**
 * 数据的来源：channel数据是由channel服务启动阶段，根据自己的配置文件保存在内存中的。
 * 数据库记录的生成：它们的数据由system进程向各channel服务进程收集，并保存到数据库中。
 * 数据库记录的消费：device服务进程和调度服务进程，从数据库中读取并消费
 */
@RestController
@RequestMapping("/kernel/manager/channel")
public class ChannelManageController {
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(ChannelEntity.class, (Object value) -> {
                ChannelEntity entity = (ChannelEntity) value;

                boolean result = true;

                if (body.containsKey(ChannelVOFieldConstant.field_channel_name)) {
                    result = entity.getChannelName().contains((String) body.get(ChannelVOFieldConstant.field_channel_name));
                }
                if (body.containsKey(ChannelVOFieldConstant.field_channel_type)) {
                    result &= entity.getChannelType().equals(body.get(ChannelVOFieldConstant.field_channel_type));
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
    public AjaxResult queryChannelType() {
        Set<String> channelTypes = new HashSet<>();

        // 正在运行的channel类型的进程
        List<Map<String, Object>> processList = this.serviceStatus.getDataList(60 * 1000);
        for (Map<String, Object> process : processList) {
            String modelType = (String) process.get(RedisStatusConstant.field_model_type);
            if (!RedisStatusConstant.value_model_type_channel.equals(modelType)) {
                continue;
            }

            String modelName = (String) process.get(RedisStatusConstant.field_model_name);
            if (MethodUtils.hasEmpty(modelName)) {
                continue;
            }

            channelTypes.add(modelName);
        }

        // 到数据库中查找同类数据：历史上启动过，但现在停止运行的channel进程
        List<Map<String, Object>> optionList = this.foxSqlService.selectOptionList("tb_channel", "channel_type", false);
        for (Map<String, Object> process : optionList) {
            String channelType = (String) process.get("channel_type");
            if (channelType == null || channelType.isEmpty()) {
                continue;
            }

            channelTypes.add(channelType);
        }

        return AjaxResult.success(channelTypes);
    }

    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        if (id == null) {
            return AjaxResult.error("输入的id为null!");
        }
        ChannelEntity exist = this.entityManageService.getEntity(id, ChannelEntity.class);
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
            String channelType = (String) params.get(ChannelVOFieldConstant.field_channel_type);
            String channelName = (String) params.get(ChannelVOFieldConstant.field_channel_name);
            Map<String, Object> channelParam = (Map<String, Object>) params.get(ChannelVOFieldConstant.field_channel_param);
            Map<String, Object> extendParam = (Map<String, Object>) params.get(ChannelVOFieldConstant.field_extend_param);

            // 简单校验参数
            if (MethodUtils.hasNull(channelType, channelName, channelParam, extendParam)) {
                return AjaxResult.error("参数不能为空:channelType, channelName, channelParam, extendParam");
            }

            // 构造作为参数的实体
            ChannelEntity entity = new ChannelEntity();
            entity.setChannelType(channelType);
            entity.setChannelName(channelName);
            entity.setChannelParam(channelParam);
            entity.setExtendParam(extendParam);


            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                ChannelEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), ChannelEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                ChannelEntity exist = this.entityManageService.getEntity(id, ChannelEntity.class);
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
        ChannelEntity exist = this.entityManageService.getChannelEntity(id);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), ChannelEntity.class);
        }

        return AjaxResult.success();
    }

    @PostMapping("status")
    public AjaxResult queryChannelStatus(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        List<Map<String, Object>> channelKeyList = (List<Map<String, Object>>) body.get(ChannelVOFieldConstant.field_channel_keys);

        // 简单校验参数
        if (MethodUtils.hasEmpty(channelKeyList)) {
            return AjaxResult.error("参数不能为空:channelKey");
        }

        try {
            // 按channelType进行分类组织
            Map<String, Set<String>> type2names = new HashMap<>();
            for (Map<String, Object> channelKey : channelKeyList) {
                String channelType = (String) channelKey.get(ChannelVOFieldConstant.field_channel_type);
                String channelName = (String) channelKey.get(ChannelVOFieldConstant.field_channel_name);
                if (MethodUtils.hasEmpty(channelType, channelName)) {
                    continue;
                }

                Set<String> channelNames = type2names.computeIfAbsent(channelType, k -> new HashSet<>());
                channelNames.add(channelName);
            }

            List<Map<String, Object>> result = new ArrayList<>();

            // 按channelType分批发送请求到各自的channel服务上进行查询
            for (String channelType : type2names.keySet()) {
                Set<String> channelNames = type2names.get(channelType);

                // 按channelType，分批查询数据
                ChannelRespondVO respondVO = this.queryChannelStatus(channelType, channelNames);
                if (!HttpStatus.SUCCESS.equals(respondVO.getCode())) {
                    continue;
                }

                Map<String, Object> channelStatusMap = (Map<String, Object>) respondVO.getRecv();
                if (MethodUtils.hasEmpty(channelStatusMap)) {
                    continue;
                }

                for (String channelName : channelNames) {
                    Map<String, Object> status = (Map<String, Object>) channelStatusMap.getOrDefault(channelName, new HashMap<>());
                    status.put(ChannelVOConstant.value_channel_name, channelName);
                    result.add(status);
                }
            }


            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private ChannelRespondVO queryChannelStatus(String channelType, Collection<String> channelNameList) throws IOException, InterruptedException {
        Map<String, Object> param = new HashMap<>();
        param.put(ChannelVOConstant.filed_operate, ChannelVOConstant.value_operate_get_status);
        param.put(ChannelVOConstant.filed_param, channelNameList);

        ChannelRequestVO requestVO = new ChannelRequestVO();
        requestVO.setUuid(UUID.randomUUID().toString());
        requestVO.setType(channelType);
        requestVO.setMode(ChannelRequestVO.MODE_MANAGE);
        requestVO.setSend(param);


        String body = JsonUtils.buildJson(requestVO);

        // 重置信号
        SyncFlagObjectMap.inst().reset(requestVO.getUuid());

        // 发送数据
        this.publisher.sendMessage(RedisTopicConstant.topic_channel_request + channelType, body);

        // 等待消息的到达：根据动态key
        ChannelRespondVO respond = (ChannelRespondVO) SyncFlagObjectMap.inst().waitDynamic(requestVO.getUuid(), 2 * 1000);
        if (respond == null) {
            return ChannelRespondVO.error("服务响应超时！");
        }

        return respond;
    }
}
