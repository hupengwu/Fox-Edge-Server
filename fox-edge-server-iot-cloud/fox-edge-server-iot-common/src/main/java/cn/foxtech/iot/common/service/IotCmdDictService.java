package cn.foxtech.iot.common.service;

import cn.foxtech.common.entity.constant.IotTemplateVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceTemplateEntity;
import cn.foxtech.common.entity.entity.IotTemplateEntity;
import cn.foxtech.common.entity.entity.OperateManualTaskEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.iot.common.entity.IotCmdNbiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class IotCmdDictService {
    private static final Logger logger = LoggerFactory.getLogger(IotCmdDictService.class);

    @Autowired
    private EntityManageService entityManageService;


    private OperateManualTaskEntity buildOperateTaskEntity(IotTemplateEntity templateEntity) {
        String iotName = templateEntity.getIotName();
        String subsetName = templateEntity.getSubsetName();

        Map<String, Object> cloudApiParam = (Map<String, Object>) templateEntity.getTemplateParam().get("cloudApiParam");
        if (cloudApiParam == null) {
            throw new ServiceException("北向模板参数格式不正确：iotName=" + iotName + ",subsetName=" + subsetName);
        }

        String paraKey = (String) cloudApiParam.get("paraKey");

        Map<String, Object> operateParam = (Map<String, Object>) templateEntity.getTemplateParam().get("operateParam");
        if (operateParam == null) {
            throw new ServiceException("北向模板参数格式不正确：iotName=" + iotName + ",subsetName=" + subsetName + ",paraKey=" + paraKey);
        }

        // 取出参数
        String manufacturer = (String) operateParam.get("manufacturer");
        String deviceType = (String) operateParam.get("deviceType");
        String templateName = (String) operateParam.get("templateName");
        String templateType = (String) operateParam.get("templateType");
        if (MethodUtils.hasEmpty(manufacturer, deviceType, subsetName, templateName, templateType)) {
            throw new ServiceException("北向模板参数格式不正确：iotName=" + iotName + ",subsetName=" + subsetName + ",paraKey=" + paraKey);
        }

        DeviceTemplateEntity find = new DeviceTemplateEntity();
        find.setSubsetName(subsetName);
        find.setTemplateType(templateType);
        find.setTemplateName(templateName);
        find.setManufacturer(manufacturer);
        find.setDeviceType(deviceType);
        DeviceTemplateEntity exist = this.entityManageService.getEntity(find.makeServiceKey(), DeviceTemplateEntity.class);
        if (exist == null) {
            throw new ServiceException("北向模板找不到绑定的设备模板：iotName=" + iotName + ",subsetName=" + subsetName + ",paraKey=" + paraKey);
        }

        String operateMode = (String) exist.getTemplateParam().get("operateMode");
        String operateName = (String) exist.getTemplateParam().get("operateName");
        Integer timeout = (Integer) exist.getTemplateParam().get("timeout");
        Map<String, Object> param = (Map<String, Object>) exist.getTemplateParam().get("param");

        // 参数检查
        if (MethodUtils.hasEmpty(manufacturer, deviceType, operateMode, operateName, timeout)) {
            throw new ServiceException("北向模板绑定的设备模板，参数格式不正确：iotName=" + iotName + ",subsetName=" + subsetName + ",paraKey=" + paraKey);
        }
        if (param == null) {
            throw new ServiceException("北向模板绑定的设备模板，参数格式不正确：iotName=" + iotName + ",subsetName=" + subsetName + ",paraKey=" + paraKey);
        }

        OperateManualTaskEntity operateManualTaskEntity = new OperateManualTaskEntity();
        operateManualTaskEntity.setManufacturer(manufacturer);
        operateManualTaskEntity.setDeviceType(deviceType);
        operateManualTaskEntity.getTaskParam().add(exist.getTemplateParam());

        return operateManualTaskEntity;
    }

    private IotTemplateEntity findIotTemplateEntity(String manufacturer, String deviceType, String iotName, String subsetName, String paraKey) {
        return this.entityManageService.getEntity(IotTemplateEntity.class, (Object value) -> {
            IotTemplateEntity entity = (IotTemplateEntity) value;

            if (!iotName.equals(entity.getIotName())) {
                return false;
            }
            if (!IotTemplateVOFieldConstant.value_operate_param.equals(entity.getTemplateType())) {
                return false;
            }

            Map<String, Object> cloudApiParam = (Map<String, Object>) entity.getTemplateParam().get("cloudApiParam");
            if (cloudApiParam == null) {
                return false;
            }

            // 构造NBI参数
            IotCmdNbiEntity nbiEntity = this.buildNbiEntity(cloudApiParam);
            if (nbiEntity == null) {
                return false;
            }

            if (paraKey.equals(nbiEntity.getParaKey())) {
                return false;
            }

            Map<String, Object> operateParam = (Map<String, Object>) entity.getTemplateParam().get("operateParam");
            if (operateParam == null) {
                return false;
            }
            if (!subsetName.equals(operateParam.get("subsetName"))) {
                return false;
            }
            if (!manufacturer.equals(operateParam.get("manufacturer"))) {
                return false;
            }
            return deviceType.equals(operateParam.get("deviceType"));
        });
    }

    public TaskRequestVO buildTaskRequestVO(DeviceEntity deviceEntity, String iotName, String subsetName, String paraKey, Object value) {
        try {
            if (MethodUtils.hasEmpty(paraKey, value)) {
                return null;
            }

            String manufacturer = deviceEntity.getManufacturer();
            String deviceType = deviceEntity.getDeviceType();

            // 查找北向模板
            IotTemplateEntity templateEntity = this.findIotTemplateEntity(manufacturer, deviceType, iotName, subsetName, paraKey);
            if (templateEntity == null) {
                return null;
            }

            // 从北向模板中，取出cloudApiParam参数
            Map<String, Object> cloudApiParam = (Map<String, Object>) templateEntity.getTemplateParam().get("cloudApiParam");
            if (cloudApiParam == null) {
                return null;
            }

            // 将cloudApiParam参数，构造成IotCmdNbiEntity
            IotCmdNbiEntity nbiEntity = this.buildNbiEntity(cloudApiParam);
            if (nbiEntity == null) {
                return null;
            }

            OperateManualTaskEntity taskEntity = this.buildOperateTaskEntity(templateEntity);


            Map<String, Object> taskParam = taskEntity.getTaskParam().get(0);
            String operateMode = (String) taskParam.get("operateMode");
            String operateName = (String) taskParam.get("operateName");
            Integer timeout = (Integer) taskParam.get("timeout");
            Map<String, Object> param = (Map<String, Object>) taskParam.get("param");

            // 转换参数
            Map<String, Object> operateParam = this.buildOperateParam(nbiEntity, param, value);

            return buildTaskRequestVO(deviceEntity, operateMode, operateName, operateParam, timeout);
        } catch (Exception e) {
            return null;
        }
    }

    private IotCmdNbiEntity buildNbiEntity(Map<String, Object> data) {
        String paraKey = (String) data.get("paraKey");
        String dataKey = (String) data.get("dataKey");
        String dataType = (String) data.get("dataType");
        Map<String, Object> enumPara = (Map<String, Object>) data.get("enumPara");

        // 检查：必填参数
        if (MethodUtils.hasEmpty(paraKey, dataKey, dataType)) {
            return null;
        }

        IotCmdNbiEntity entity = new IotCmdNbiEntity();
        entity.setParaKey(paraKey);
        entity.setDataKey(dataKey);
        entity.setDataType(dataType);

        if (dataType.equals("enum")) {
            Map<Object, Object> enums = this.buildEnum(enumPara);
            if (enums == null) {
                return null;
            }
            entity.setEnumPara(enums);
        }


        return entity;
    }

    private Map<Object, Object> buildEnum(Map<String, Object> enumPara) {
        String keyType = (String) enumPara.get("keyType");
        String valType = (String) enumPara.get("valType");
        Map<String, Object> enums = (Map<String, Object>) enumPara.get("enum");
        if (MethodUtils.hasEmpty(keyType, valType, enums)) {
            return null;
        }

        Map<Object, Object> result = new HashMap<>();
        for (String key : enums.keySet()) {
            Object value = enums.get(key);

            Object keyObj = this.buildValue(keyType, key);
            Object valObj = this.buildValue(valType, value);

            if (keyObj == null || valObj == null) {
                return null;
            }

            result.put(keyObj, valObj);
        }

        return result;
    }

    private Object buildValue(String dataType, Object value) {
        if ("int".equalsIgnoreCase(dataType) || "int32".equalsIgnoreCase(dataType)) {
            value = Integer.parseInt(value.toString());
        }
        if ("long".equalsIgnoreCase(dataType) || "int64".equalsIgnoreCase(dataType)) {
            value = Long.parseLong(value.toString());
        }
        if ("float".equalsIgnoreCase(dataType) || "float32".equalsIgnoreCase(dataType)) {
            value = Float.parseFloat(value.toString());
        }
        if ("double".equalsIgnoreCase(dataType) || "float64".equalsIgnoreCase(dataType)) {
            value = Double.parseDouble(value.toString());
        }
        if ("bool".equalsIgnoreCase(dataType) || "boolean".equalsIgnoreCase(dataType)) {
            value = Boolean.parseBoolean(value.toString());
        }
        if ("string".equalsIgnoreCase(dataType)) {
            value = value.toString();
        }

        return value;
    }

    private Map<String, Object> buildOperateParam(IotCmdNbiEntity nbi, Map<String, Object> param, Object value) {
        Map<String, Object> operateParam = JsonUtils.clone(param);

        // 对简单参数进行转换
        value = this.buildValue(nbi.getDataType(), value);

        // 对enum类型进行转换
        if (nbi.getDataType().equals("enum")) {
            if (nbi.getEnumPara().containsKey(value)) {
                value = nbi.getEnumPara().get(value);
            }
        }

        operateParam.put(nbi.getDataKey(), value);
        return operateParam;
    }

    private TaskRequestVO buildTaskRequestVO(DeviceEntity deviceEntity, String operateMode, String operateName, Map<String, Object> operateParam, Integer timeout) {
        // 根据模板的参数，开始构造发送给设备的批量服务请求
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.setClientName("iot-cmd");
        taskRequestVO.setUuid(UUID.randomUUID().toString());

        // 操作命令
        OperateRequestVO operateRequestVO = new OperateRequestVO();
        operateRequestVO.setManufacturer(deviceEntity.getManufacturer());
        operateRequestVO.setDeviceType(deviceEntity.getDeviceType());
        operateRequestVO.setDeviceName(deviceEntity.getDeviceName());
        operateRequestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));
        operateRequestVO.setOperateMode(operateMode);
        operateRequestVO.setOperateName(operateName);
        operateRequestVO.getParam().putAll(operateParam);
        operateRequestVO.getParam().putAll(deviceEntity.getDeviceParam());
        operateRequestVO.setTimeout(timeout);
        operateRequestVO.setRecord(true);

        taskRequestVO.getRequestVOS().add(operateRequestVO);
        taskRequestVO.setTimeout(operateRequestVO.getTimeout());

        return taskRequestVO;
    }
}
