/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.DeviceTemplateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceTemplateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepoLocalDevTemplateService {
    @Autowired
    private EntityManageService entityManageService;


    public List<BaseEntity> selectEntityList(Map<String, Object> body) {
        return this.entityManageService.getEntityList(DeviceTemplateEntity.class, (Object value) -> {
            DeviceTemplateEntity entity = (DeviceTemplateEntity) value;

            boolean result = true;

            if (body.containsKey(DeviceTemplateVOFieldConstant.field_manufacturer)) {
                result &= entity.getManufacturer().equals(body.get(DeviceTemplateVOFieldConstant.field_manufacturer));
            }
            if (body.containsKey(DeviceTemplateVOFieldConstant.field_device_type)) {
                result &= entity.getDeviceType().contains((String) body.get(DeviceTemplateVOFieldConstant.field_device_type));
            }
            if (body.containsKey(DeviceTemplateVOFieldConstant.field_subset_name)) {
                result &= entity.getSubsetName().equals(body.get(DeviceTemplateVOFieldConstant.field_subset_name));
            }
            if (body.containsKey(DeviceTemplateVOFieldConstant.field_template_type)) {
                result &= entity.getTemplateType().equals(body.get(DeviceTemplateVOFieldConstant.field_template_type));
            }
            if (body.containsKey(DeviceTemplateVOFieldConstant.field_template_name)) {
                result &= entity.getTemplateName().equals(body.get(DeviceTemplateVOFieldConstant.field_template_name));
            }

            return result;
        });
    }

    public List<BaseEntity> getDeviceTemplateEntityList(RepoCompEntity compEntity, String templateType) {
        return this.entityManageService.getEntityList(DeviceTemplateEntity.class, (Object value) -> {
            DeviceTemplateEntity entity = (DeviceTemplateEntity) value;

            String manufacturer = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_manufacturer);
            String deviceType = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_device_type);
            String subsetName = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_subset_name);

            if (!entity.getTemplateType().equals(templateType)) {
                return false;
            }
            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }
            if (!entity.getDeviceType().equals(deviceType)) {
                return false;
            }
            return entity.getSubsetName().equals(subsetName);

        });
    }

    public List<BaseEntity> getDeviceTemplateChannelList(RepoCompEntity compEntity, String channelType) {
        return this.entityManageService.getEntityList(DeviceTemplateEntity.class, (Object value) -> {
            DeviceTemplateEntity entity = (DeviceTemplateEntity) value;

            String manufacturer = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_manufacturer);
            String deviceType = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_device_type);
            String subsetName = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_subset_name);

            if (!entity.getTemplateType().equals(DeviceTemplateVOFieldConstant.value_channel_param)) {
                return false;
            }
            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }
            if (!entity.getDeviceType().equals(deviceType)) {
                return false;
            }
            if (!entity.getSubsetName().equals(subsetName)) {
                return false;
            }
            return channelType.equals(entity.getTemplateParam().get("channelType"));

        });
    }

    public List<BaseEntity> getDeviceTemplateEntityList(String manufacturer, String deviceType, String subsetName) {
        return this.entityManageService.getEntityList(DeviceTemplateEntity.class, (Object value) -> {
            DeviceTemplateEntity entity = (DeviceTemplateEntity) value;

            if (!manufacturer.equals(entity.getManufacturer())) {
                return false;
            }
            if (!deviceType.equals(entity.getDeviceType())) {
                return false;
            }
            return subsetName.equals(entity.getSubsetName());

        });
    }

    public List<BaseEntity> getDeviceTemplateEntityList(RepoCompEntity compEntity) {
        String manufacturer = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_manufacturer);
        String deviceType = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_device_type);
        String subsetName = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_subset_name);


        return this.getDeviceTemplateEntityList(manufacturer, deviceType, subsetName);
    }

    public void insertOrUpdate(Map<String, Object> params) {
        // 提取业务参数
        Integer compId = (Integer) params.get(DeviceTemplateVOFieldConstant.field_comp_id);
        String templateName = (String) params.get(DeviceTemplateVOFieldConstant.field_template_name);
        String templateType = (String) params.get(DeviceTemplateVOFieldConstant.field_template_type);

        Map<String, Object> templateParam = (Map<String, Object>) params.get(DeviceTemplateVOFieldConstant.field_template_param);
        Map<String, Object> extendParam = (Map<String, Object>) params.get(DeviceTemplateVOFieldConstant.field_extend_param);

        // 简单校验参数
        if (MethodUtils.hasEmpty(compId, templateType, templateName)) {
            throw new ServiceException("参数不能为空: compId, templateType, templateName");
        }


        RepoCompEntity compEntity = this.entityManageService.getEntity(Long.valueOf(compId), RepoCompEntity.class);
        if (compEntity == null) {
            throw new ServiceException("找不到对应的组件:" + compId);
        }

        String manufacturer = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_manufacturer);
        String deviceType = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_device_type);
        String subsetName = (String) compEntity.getCompParam().get(DeviceTemplateVOFieldConstant.field_subset_name);
        if (MethodUtils.hasEmpty(manufacturer, deviceType, subsetName)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType, subsetName");
        }

        // 验证模板类型
        if (!templateType.equals(DeviceTemplateVOFieldConstant.value_device_param) && !templateType.equals(DeviceTemplateVOFieldConstant.value_operate_param) && !templateType.equals(DeviceTemplateVOFieldConstant.value_channel_param)) {
            throw new ServiceException("类型不正确");
        }
        if (templateType.equals(DeviceTemplateVOFieldConstant.value_device_param) && params.get("id") == null && this.getDeviceTemplateEntityList(compEntity, templateType).size() > 0) {
            throw new ServiceException("设备参数在同一个子集中，最多只能创建一个");
        }
        if (templateType.equals(DeviceTemplateVOFieldConstant.value_channel_param) && params.get("id") == null && this.getDeviceTemplateChannelList(compEntity, (String) templateParam.getOrDefault("channelType", "")).size() > 0) {
            throw new ServiceException("同一个通道类型的通道参数在同一个子集中，最多只能创建一个");
        }
        if (templateType.equals(DeviceTemplateVOFieldConstant.value_operate_param) && !verifyOperateParam(templateParam)) {
            throw new ServiceException("操作参数的格式不合法");
        }


        // 构造作为参数的实体
        DeviceTemplateEntity entity = new DeviceTemplateEntity();
        entity.setManufacturer(manufacturer);
        entity.setDeviceType(deviceType);
        entity.setSubsetName(subsetName);
        entity.setTemplateType(templateType);
        entity.setTemplateName(templateName);
        entity.setTemplateParam(templateParam);
        entity.setExtendParam(extendParam);

        // 简单验证实体的合法性
        if (entity.hasNullServiceKey()) {
            throw new ServiceException("具有null的service key！");
        }

        // 新增/修改实体：参数不包含id为新增，包含为修改
        if (params.get("id") == null) {
            if (entity.getTemplateParam() == null) {
                entity.setTemplateParam(new HashMap<>());
            }
            if (entity.getExtendParam() == null) {
                entity.setExtendParam(new HashMap<>());
            }

            DeviceTemplateEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), DeviceTemplateEntity.class);
            if (exist != null) {
                throw new ServiceException("实体已存在");
            }

            this.entityManageService.insertEntity(entity);
        } else {
            Long id = Long.parseLong(params.get("id").toString());
            DeviceTemplateEntity exist = this.entityManageService.getEntity(id, DeviceTemplateEntity.class);
            if (exist == null) {
                throw new ServiceException("实体不存在");
            }

            // 如果没有填写，说明只是修改标题，那么填入原来的数值
            if (MethodUtils.hasEmpty(entity.getTemplateParam())) {
                entity.setTemplateParam(exist.getTemplateParam());
            }
            // 如果为空，那么说明用户不想修改该数据
            if (MethodUtils.hasNull(entity.getExtendParam())) {
                entity.setExtendParam(exist.getExtendParam());
            }


            if (!exist.getManufacturer().equals(manufacturer) // 不允许修改
                    || !exist.getDeviceType().equals(deviceType) // 不允许修改
            ) {
                throw new ServiceException("不允许修改，否则影响关联关系：manufacturer, deviceType, engineType, operateName");
            }

            // 修改数据
            entity.setId(id);
            this.entityManageService.updateEntity(entity);
        }
    }

    private boolean verifyOperateParam(Map<String, Object> templateParam) {
        try {
            Map<String, Object> param = (Map<String, Object>) templateParam.get("param");
            String operateMode = (String) templateParam.get("operateMode");
            String operateName = (String) templateParam.get("operateName");
            Integer timeout = (Integer) templateParam.get("timeout");
            if (MethodUtils.hasEmpty(operateName, operateMode)) {
                return false;
            }
            return !MethodUtils.hasNull(param, timeout);
        } catch (Exception e) {
            return false;
        }

    }

    public void deleteEntity(Long id) {
        this.entityManageService.deleteEntity(id, DeviceTemplateEntity.class);
    }

    public DeviceTemplateEntity queryEntity(Long id) {
        return this.entityManageService.getEntity(id, DeviceTemplateEntity.class);
    }

    public RepoCompEntity getCompEntity(String manufacturer, String deviceType, String subsetName) {
        RepoCompEntity compEntity = this.entityManageService.getEntity(RepoCompEntity.class, (Object value) -> {
            RepoCompEntity entity = (RepoCompEntity) value;

            if (!entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_dev_template)) {
                return false;
            }

            if (!manufacturer.equals(entity.getCompParam().get(RepoCompConstant.field_manufacturer))) {
                return false;
            }
            if (!deviceType.equals(entity.getCompParam().get(RepoCompConstant.field_device_type))) {
                return false;
            }

            return subsetName.equals(entity.getCompParam().get(DeviceTemplateVOFieldConstant.field_subset_name));
        });

        return compEntity;
    }

    public List<Map<String, Object>> selectOptionList(Map<String, Object> body) {
        String field = (String) body.get("field");
        String manufacturer = (String) body.get("manufacturer");
        String deviceType = (String) body.get("deviceType");
        String templateType = (String) body.get("templateType");
        String subsetName = (String) body.get("subsetName");
        String operateName = (String) body.get("operateName");
        String channelType = (String) body.get("channelType");

        if (MethodUtils.hasEmpty(field, manufacturer, deviceType, templateType)) {
            throw new ServiceException("参数缺失：field, manufacturer, deviceType, templateType");
        }


        List<Map<String, Object>> resultList = new ArrayList<>();
        this.entityManageService.getEntityList(DeviceTemplateEntity.class, (Object value) -> {
            DeviceTemplateEntity entity = (DeviceTemplateEntity) value;

            if (!templateType.equals(entity.getTemplateType())) {
                return false;
            }
            if (!manufacturer.equals(entity.getManufacturer())) {
                return false;
            }
            if (!deviceType.equals(entity.getDeviceType())) {
                return false;
            }

            // 场景1：创建设备时，下拉设备类型列表，会传递这两个参数
            if (field.equals("subsetName") && templateType.equals(DeviceTemplateVOFieldConstant.value_device_param)) {
                Map<String, Object> option = new HashMap<>();
                option.put("value", entity.getSubsetName());
                option.put("label", entity.getSubsetName());

                resultList.add(option);
                return false;
            }
            // 场景2：创建操作任务时，下拉设备类型列表，会传递这两个参数
            if (field.equals("subsetName") && templateType.equals(DeviceTemplateVOFieldConstant.value_operate_param)) {
                if (MethodUtils.hasEmpty(operateName)) {
                    return false;
                }
                if (!operateName.equals(entity.getTemplateParam().get("operateName"))) {
                    return false;
                }

                Map<String, Object> option = new HashMap<>();
                option.put("value", entity.getSubsetName());
                option.put("label", entity.getSubsetName());

                if (resultList.contains(option)) {
                    return false;
                }

                resultList.add(option);
                return false;
            }
            // 场景3：创建操作任务时，下拉子集列表，会传递这两个参数
            if (field.equals("templateName") && templateType.equals(DeviceTemplateVOFieldConstant.value_operate_param)) {
                if (!entity.getSubsetName().equals(subsetName)) {
                    return false;
                }
                if (MethodUtils.hasEmpty(operateName)) {
                    return false;
                }
                if (!operateName.equals(entity.getTemplateParam().get("operateName"))) {
                    return false;
                }

                Map<String, Object> option = new HashMap<>();
                option.put("value", entity.getTemplateName());
                option.put("label", entity.getTemplateName());

                resultList.add(option);
                return false;
            }

            // 场景4：创建通道时，下拉子集列表，会传递这两个参数
            if (field.equals("templateName") && templateType.equals(DeviceTemplateVOFieldConstant.value_channel_param)) {
                if (!entity.getSubsetName().equals(subsetName)) {
                    return false;
                }
                if (MethodUtils.hasEmpty(channelType)) {
                    return false;
                }
                if (!channelType.equals(entity.getTemplateParam().get("channelType"))) {
                    return false;
                }

                Map<String, Object> option = new HashMap<>();
                option.put("value", entity.getTemplateName());
                option.put("label", entity.getTemplateName());

                resultList.add(option);
                return false;
            }


            return false;
        });

        return resultList;
    }

    public List<Map<String, Object>> selectGroupList(Map<String, Object> body) {
        String manufacturer = (String) body.get("manufacturer");
        String deviceType = (String) body.get("deviceType");
        String subsetName = (String) body.get("subsetName");

        Map<String, Object> sums = new HashMap<>();
        sums.put(DeviceTemplateVOFieldConstant.value_operate_param, 0);
        sums.put(DeviceTemplateVOFieldConstant.value_device_param, 0);
        sums.put(DeviceTemplateVOFieldConstant.value_channel_param, 0);

        this.entityManageService.getEntityList(DeviceTemplateEntity.class, (Object value) -> {
            DeviceTemplateEntity entity = (DeviceTemplateEntity) value;

            if (!manufacturer.equals(entity.getManufacturer())) {
                return false;
            }
            if (!deviceType.equals(entity.getDeviceType())) {
                return false;
            }
            if (!subsetName.equals(entity.getSubsetName())) {
                return false;
            }

            if (sums.containsKey(entity.getTemplateType())) {
                Integer sum = (Integer) sums.get(entity.getTemplateType());
                sum++;
                sums.put(entity.getTemplateType(), sum);
            }

            return false;
        });

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String key : sums.keySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put(DeviceTemplateVOFieldConstant.field_template_type, key);
            data.put("count", sums.get(key));

            data.put("manufacturer", manufacturer);
            data.put("deviceType", deviceType);
            data.put("subsetName", subsetName);

            resultList.add(data);
        }
        return resultList;
    }
}
