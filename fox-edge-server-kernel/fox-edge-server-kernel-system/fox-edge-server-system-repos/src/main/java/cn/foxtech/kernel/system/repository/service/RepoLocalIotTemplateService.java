/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.IotTemplateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.IotTemplateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepoLocalIotTemplateService {
    @Autowired
    private EntityManageService entityManageService;


    public List<BaseEntity> selectEntityList(Map<String, Object> body) {
        return this.entityManageService.getEntityList(IotTemplateEntity.class, (Object value) -> {
            IotTemplateEntity entity = (IotTemplateEntity) value;

            boolean result = true;

            if (body.containsKey(IotTemplateVOFieldConstant.field_iot_name)) {
                result &= entity.getIotName().equals(body.get(IotTemplateVOFieldConstant.field_iot_name));
            }
            if (body.containsKey(IotTemplateVOFieldConstant.field_subset_name)) {
                result &= entity.getSubsetName().equals(body.get(IotTemplateVOFieldConstant.field_subset_name));
            }
            if (body.containsKey(IotTemplateVOFieldConstant.field_template_type)) {
                result &= entity.getTemplateType().equals(body.get(IotTemplateVOFieldConstant.field_template_type));
            }
            if (body.containsKey(IotTemplateVOFieldConstant.field_template_name)) {
                result &= entity.getTemplateName().equals(body.get(IotTemplateVOFieldConstant.field_template_name));
            }

            return result;
        });
    }

    public List<BaseEntity> getIotTemplateEntityList(RepoCompEntity compEntity, String templateType) {
        return this.entityManageService.getEntityList(IotTemplateEntity.class, (Object value) -> {
            IotTemplateEntity entity = (IotTemplateEntity) value;

            String iotName = (String) compEntity.getCompParam().get(IotTemplateVOFieldConstant.field_iot_name);
            String subsetName = (String) compEntity.getCompParam().get(IotTemplateVOFieldConstant.field_subset_name);

            if (!entity.getTemplateType().equals(templateType)) {
                return false;
            }
            if (!entity.getIotName().equals(iotName)) {
                return false;
            }

            return entity.getSubsetName().equals(subsetName);

        });
    }

    public List<BaseEntity> getIotTemplateEntityList(String iotName, String subsetName) {
        return this.entityManageService.getEntityList(IotTemplateEntity.class, (Object value) -> {
            IotTemplateEntity entity = (IotTemplateEntity) value;

            if (!iotName.equals(entity.getIotName())) {
                return false;
            }
            return subsetName.equals(entity.getSubsetName());

        });
    }

    public List<BaseEntity> getIotTemplateEntityList(RepoCompEntity compEntity) {
        String iotName = (String) compEntity.getCompParam().get(IotTemplateVOFieldConstant.field_iot_name);
        String subsetName = (String) compEntity.getCompParam().get(IotTemplateVOFieldConstant.field_subset_name);


        return this.getIotTemplateEntityList(iotName, subsetName);
    }

    public void insertOrUpdate(Map<String, Object> params) {
        // 提取业务参数
        Integer compId = (Integer) params.get(IotTemplateVOFieldConstant.field_comp_id);
        String templateName = (String) params.get(IotTemplateVOFieldConstant.field_template_name);
        String templateType = (String) params.get(IotTemplateVOFieldConstant.field_template_type);

        Map<String, Object> templateParam = (Map<String, Object>) params.get(IotTemplateVOFieldConstant.field_template_param);
        Map<String, Object> extendParam = (Map<String, Object>) params.get(IotTemplateVOFieldConstant.field_extend_param);

        // 简单校验参数
        if (MethodUtils.hasEmpty(compId, templateType, templateName)) {
            throw new ServiceException("参数不能为空: compId, templateType, templateName");
        }


        RepoCompEntity compEntity = this.entityManageService.getEntity(Long.valueOf(compId), RepoCompEntity.class);
        if (compEntity == null) {
            throw new ServiceException("找不到对应的组件:" + compId);
        }

        String iotName = (String) compEntity.getCompParam().get(IotTemplateVOFieldConstant.field_iot_name);
        String subsetName = (String) compEntity.getCompParam().get(IotTemplateVOFieldConstant.field_subset_name);
        if (MethodUtils.hasEmpty(iotName, subsetName)) {
            throw new ServiceException("参数不能为空: iotName, subsetName");
        }

        // 验证模板类型
        if (!templateType.equals(IotTemplateVOFieldConstant.value_device_param) && !templateType.equals(IotTemplateVOFieldConstant.value_operate_param)) {
            throw new ServiceException("类型不正确");
        }
        if (templateType.equals(IotTemplateVOFieldConstant.value_device_param) && params.get("id") == null && this.getIotTemplateEntityList(compEntity, templateType).size() > 0) {
            throw new ServiceException("设备参数在同一个子集中，最多只能创建一个");
        }
        if (templateType.equals(IotTemplateVOFieldConstant.value_operate_param) && !verifyOperateParam(templateParam)) {
            throw new ServiceException("操作参数的格式不合法");
        }


        // 构造作为参数的实体
        IotTemplateEntity entity = new IotTemplateEntity();
        entity.setIotName(iotName);
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

            IotTemplateEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), IotTemplateEntity.class);
            if (exist != null) {
                throw new ServiceException("实体已存在");
            }

            this.entityManageService.insertEntity(entity);
        } else {
            Long id = Long.parseLong(params.get("id").toString());
            IotTemplateEntity exist = this.entityManageService.getEntity(id, IotTemplateEntity.class);
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


            if (!exist.makeServiceKey().equals(entity.makeServiceKey()) // 不允许修改
            ) {
                throw new ServiceException("不允许修改serviceKey");
            }

            // 修改数据
            entity.setId(id);
            this.entityManageService.updateEntity(entity);
        }
    }

    private boolean verifyOperateParam(Map<String, Object> templateParam) {
        try {
            Map<String, Object> operateParam = (Map<String, Object>) templateParam.get("operateParam");
            Map<String, Object> cloudApiParam = (Map<String, Object>) templateParam.get("cloudApiParam");
            if (MethodUtils.hasEmpty(operateParam, cloudApiParam)) {
                return false;
            }

            String deviceType = (String) operateParam.get("deviceType");
            String manufacturer = (String) operateParam.get("manufacturer");
            String subsetName = (String) operateParam.get("subsetName");
            String templateName = (String) operateParam.get("templateName");
            String templateType = (String) operateParam.get("templateType");

            String dataKey = (String) cloudApiParam.get("dataKey");
            String paraKey = (String) cloudApiParam.get("paraKey");
            String dataType = (String) cloudApiParam.get("dataType");

            return !MethodUtils.hasEmpty(deviceType, manufacturer, subsetName, templateName, templateType, dataKey, paraKey, dataType);
        } catch (Exception e) {
            return false;
        }

    }

    public void deleteEntity(Long id) {
        this.entityManageService.deleteEntity(id, IotTemplateEntity.class);
    }

    public IotTemplateEntity queryEntity(Long id) {
        return this.entityManageService.getEntity(id, IotTemplateEntity.class);
    }

    public RepoCompEntity getCompEntity(String iotName, String subsetName) {
        RepoCompEntity compEntity = this.entityManageService.getEntity(RepoCompEntity.class, (Object value) -> {
            RepoCompEntity entity = (RepoCompEntity) value;

            if (!entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_iot_template)) {
                return false;
            }

            if (!iotName.equals(entity.getCompParam().get(IotTemplateVOFieldConstant.field_iot_name))) {
                return false;
            }

            return subsetName.equals(entity.getCompParam().get(IotTemplateVOFieldConstant.field_subset_name));
        });

        return compEntity;
    }

    public List<Map<String, Object>> selectOptionList(Map<String, Object> body) {
        String field = (String) body.get("field");
        String iotName = (String) body.get("iotName");
        String templateType = (String) body.get("templateType");
        String subsetName = (String) body.get("subsetName");
        String operateName = (String) body.get("operateName");

        if (MethodUtils.hasEmpty(field, iotName, templateType)) {
            throw new ServiceException("参数缺失：field, iotName, templateType");
        }


        List<Map<String, Object>> resultList = new ArrayList<>();
        this.entityManageService.getEntityList(IotTemplateEntity.class, (Object value) -> {
            IotTemplateEntity entity = (IotTemplateEntity) value;

            if (!templateType.equals(entity.getTemplateType())) {
                return false;
            }
            if (!iotName.equals(entity.getIotName())) {
                return false;
            }

            // 场景1：创建设备时，下拉设备类型列表，会传递这两个参数
            if (field.equals("subsetName") && templateType.equals(IotTemplateVOFieldConstant.value_device_param)) {
                Map<String, Object> option = new HashMap<>();
                option.put("value", entity.getSubsetName());
                option.put("label", entity.getSubsetName());

                resultList.add(option);
                return false;
            }
            // 场景2：创建操作任务时，下拉设备类型列表，会传递这两个参数
            if (field.equals("subsetName") && templateType.equals(IotTemplateVOFieldConstant.value_operate_param)) {
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
            if (field.equals("templateName") && templateType.equals(IotTemplateVOFieldConstant.value_operate_param)) {
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

            return false;
        });

        return resultList;
    }

    public List<Map<String, Object>> selectGroupList(Map<String, Object> body) {
        String iotName = (String) body.get("iotName");
        String subsetName = (String) body.get("subsetName");

        Map<String, Object> sums = new HashMap<>();
        sums.put(IotTemplateVOFieldConstant.value_operate_param, 0);
        sums.put(IotTemplateVOFieldConstant.value_device_param, 0);

        this.entityManageService.getEntityList(IotTemplateEntity.class, (Object value) -> {
            IotTemplateEntity entity = (IotTemplateEntity) value;

            if (!iotName.equals(entity.getIotName())) {
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
            data.put(IotTemplateVOFieldConstant.field_template_type, key);
            data.put("count", sums.get(key));

            data.put("iotName", iotName);
            data.put("subsetName", subsetName);

            resultList.add(data);
        }
        return resultList;
    }
}
