package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepoLocalCompSync {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private CloudRemoteService remoteService;

    /**
     * 从远程的fox-cloud中，同步信息groupName/compId到本地组件
     *
     * @param compId
     * @return
     * @throws IOException
     */
    public Map<String, Object> syncEntity(Long compId) throws IOException {
        RepoCompEntity entity = this.entityManageService.getEntity(compId, RepoCompEntity.class);
        if (entity == null) {
            throw new ServiceException("实体不存在");
        }


        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_file_template)) {
            return this.syncCsvTemplateEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jar_decoder)) {
            return this.syncJarDecoderEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            return this.syncJspDecoderEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsn_decoder)) {
            return this.syncJsnDecoderEntity(entity);
        }

        throw new ServiceException("该组件类型，不支持从云端同步！");
    }

    /**
     * 从远程的fox-cloud中，同步信息groupName/compId到本地组件
     *
     * @param entity
     * @return
     * @throws IOException
     */
    private Map<String, Object> syncCsvTemplateEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) compParam.get(RepoCompConstant.filed_model_version);
        if (MethodUtils.hasEmpty(modelName, modelVersion)) {
            throw new ServiceException("缺少参数： modelName, modelVersion");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(RepoCompConstant.filed_model_type, "template");
        body.put(RepoCompConstant.filed_model_name, modelName);
        body.put(RepoCompConstant.filed_model_version, modelVersion);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/groupName", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }
        if (data == null) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + modelName);
        }

        // 找到了云端的组件信息
        Map<String, Object> map = (Map<String, Object>) data;

        // 克隆一个副本，防止修改影响到了原本
        entity = JsonUtils.clone(entity);
        entity.getCompParam().put(RepoCompVOFieldConstant.field_group_name, map.get("groupName"));
        entity.getCompParam().put(RepoCompVOFieldConstant.field_comp_id, map.get("id"));

        // 保存数据
        this.entityManageService.updateEntity(entity);

        return respond;
    }

    private Map<String, Object> syncJarDecoderEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) compParam.get(RepoCompConstant.filed_model_version);
        if (MethodUtils.hasEmpty(modelName, modelVersion)) {
            throw new ServiceException("缺少参数： modelName, modelVersion");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(RepoCompConstant.filed_model_type, "decoder");
        body.put(RepoCompConstant.filed_model_name, modelName);
        body.put(RepoCompConstant.filed_model_version, modelVersion);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/groupName", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }
        if (data == null) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + modelName);
        }

        // 找到了云端的组件信息
        Map<String, Object> map = (Map<String, Object>) data;

        // 克隆一个副本，防止修改影响到了原本
        entity = JsonUtils.clone(entity);
        entity.getCompParam().put(RepoCompVOFieldConstant.field_group_name, map.get("groupName"));
        entity.getCompParam().put(RepoCompVOFieldConstant.field_comp_id, map.get("id"));

        // 保存数据
        this.entityManageService.updateEntity(entity);

        return respond;
    }

    private Map<String, Object> syncJspDecoderEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        if (MethodUtils.hasEmpty(deviceType, manufacturer)) {
            throw new ServiceException("缺少参数： deviceType, manufacturer");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
        body.put(OperateVOFieldConstant.field_device_type, deviceType);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/script/title/list", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code) || data == null) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }

        // 如果空列表：云端没有这个组件
        List<Map<String, Object>> list = (List<Map<String, Object>>) data;
        if (list.isEmpty()) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + deviceType);
        }

        // 找到了云端的组件信息
        Map<String, Object> map = list.get(0);

        // 克隆一个副本，防止修改影响到了原本
        entity = JsonUtils.clone(entity);
        entity.getCompParam().put(RepoCompVOFieldConstant.field_group_name, map.get("groupName"));
        entity.getCompParam().put(RepoCompVOFieldConstant.field_comp_id, map.get("id"));

        // 保存数据
        this.entityManageService.updateEntity(entity);

        return respond;
    }

    private Map<String, Object> syncJsnDecoderEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        if (MethodUtils.hasEmpty(deviceType, manufacturer)) {
            throw new ServiceException("缺少参数： deviceType, manufacturer");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
        body.put(OperateVOFieldConstant.field_device_type, deviceType);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/model/title/list", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code) || data == null) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }

        // 如果空列表：云端没有这个组件
        List<Map<String, Object>> list = (List<Map<String, Object>>) data;
        if (list.isEmpty()) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + deviceType);
        }

        // 找到了云端的组件信息
        Map<String, Object> map = list.get(0);

        // 克隆一个副本，防止修改影响到了原本
        entity = JsonUtils.clone(entity);
        entity.getCompParam().put(RepoCompVOFieldConstant.field_group_name, map.get("groupName"));
        entity.getCompParam().put(RepoCompVOFieldConstant.field_comp_id, map.get("id"));

        // 保存数据
        this.entityManageService.updateEntity(entity);

        return respond;
    }
}
