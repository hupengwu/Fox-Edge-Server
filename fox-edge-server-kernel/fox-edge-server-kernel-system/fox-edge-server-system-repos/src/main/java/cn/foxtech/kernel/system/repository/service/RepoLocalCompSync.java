/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.constant.DeviceTemplateVOFieldConstant;
import cn.foxtech.common.entity.constant.IotTemplateVOFieldConstant;
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

        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jar_decoder)) {
            return this.syncJarDecoderEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            return this.syncJspDecoderEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsn_decoder)) {
            return this.syncJsnDecoderEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_dev_template)) {
            return this.syncDevTemplateEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_iot_template)) {
            return this.syncIotTemplateEntity(entity);
        }

        throw new ServiceException("该组件类型，不支持从云端同步！");
    }

    private Map<String, Object> syncJarDecoderEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String modelName = (String) compParam.get(RepoCompConstant.field_model_name);
        if (MethodUtils.hasEmpty(modelName)) {
            throw new ServiceException("缺少参数： modelName");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(RepoCompConstant.field_model_name, modelName);
        body.put(RepoCompConstant.field_model_type, "decoder");
        body.put(RepoCompConstant.field_model_version, RepoCompConstant.field_value_model_version_default);

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

    private Map<String, Object> syncDevTemplateEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String deviceType = (String) compParam.get(DeviceTemplateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(DeviceTemplateVOFieldConstant.field_manufacturer);
        String subsetName = (String) compParam.get(DeviceTemplateVOFieldConstant.field_subset_name);
        if (MethodUtils.hasEmpty(deviceType, manufacturer, subsetName)) {
            throw new ServiceException("缺少参数： deviceType, manufacturer, subsetName");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(DeviceTemplateVOFieldConstant.field_manufacturer, manufacturer);
        body.put(DeviceTemplateVOFieldConstant.field_device_type, deviceType);
        body.put(DeviceTemplateVOFieldConstant.field_subset_name, subsetName);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/dev-template/title/list", body);
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

    private Map<String, Object> syncIotTemplateEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String iotName = (String) compParam.get(IotTemplateVOFieldConstant.field_iot_name);
        String subsetName = (String) compParam.get(IotTemplateVOFieldConstant.field_subset_name);
        if (MethodUtils.hasEmpty(iotName, subsetName)) {
            throw new ServiceException("缺少参数： iotName, subsetName");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(IotTemplateVOFieldConstant.field_iot_name, iotName);
        body.put(IotTemplateVOFieldConstant.field_subset_name, subsetName);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/iot-template/title/list", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code) || data == null) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }

        // 如果空列表：云端没有这个组件
        List<Map<String, Object>> list = (List<Map<String, Object>>) data;
        if (list.isEmpty()) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + iotName);
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
