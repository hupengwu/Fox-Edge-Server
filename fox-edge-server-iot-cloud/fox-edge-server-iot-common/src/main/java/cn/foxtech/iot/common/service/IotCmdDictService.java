package cn.foxtech.iot.common.service;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.OperateManualTaskEntity;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.iot.common.entity.IotCmdNbiEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class IotCmdDictService {
    private static final Logger logger = LoggerFactory.getLogger(IotCmdDictService.class);
    private final Map<String, Object> cmdMap = new HashMap<>();

    public void load(String filePath) throws IOException {
        File file = new File("");
        String path = FileNameUtils.getOsFilePath(file.getAbsolutePath() + "/" + filePath);
        String json = FileTextUtils.readTextFile(path);

        List<Map<String, Object>> list = JsonUtils.buildObject(json, List.class);
        for (Map<String, Object> cmd : list) {
            List<Map<String, Object>> nbiList = (List<Map<String, Object>>) cmd.get("nbi");
            Map<String, Object> taskParam = (Map<String, Object>) cmd.get("task");

            if (MethodUtils.hasEmpty(nbiList, taskParam)) {
                continue;
            }

            String manufacturer = (String) taskParam.get("manufacturer");
            String deviceType = (String) taskParam.get("deviceType");
            String operateMode = (String) taskParam.get("operateMode");
            String operateName = (String) taskParam.get("operateName");
            Integer timeout = (Integer) taskParam.get("timeout");
            Map<String, Object> param = (Map<String, Object>) taskParam.get("param");

            // 参数检查
            if (MethodUtils.hasEmpty(manufacturer, deviceType, operateMode, operateName, timeout)) {
                continue;
            }
            if (param == null) {
                continue;
            }

            OperateManualTaskEntity operateManualTaskEntity = new OperateManualTaskEntity();
            operateManualTaskEntity.setManufacturer(manufacturer);
            operateManualTaskEntity.setDeviceType(deviceType);
            operateManualTaskEntity.getTaskParam().add(taskParam);

            for (Map<String, Object> nbi : nbiList) {
                // 构造NBI参数
                IotCmdNbiEntity nbiEntity = this.buildNbiEntity(nbi);
                if (nbiEntity == null) {
                    break;
                }

                String paraKey = nbiEntity.getParaKey();
                MapUtils.setValue(this.cmdMap, manufacturer, deviceType, paraKey, "nbi", nbiEntity);
                MapUtils.setValue(this.cmdMap, manufacturer, deviceType, paraKey, "task", operateManualTaskEntity);
            }
        }
    }


    public TaskRequestVO buildTaskRequestVO(DeviceEntity deviceEntity, Object key, Object value) {
        try {
            if (MethodUtils.hasEmpty(key, value)) {
                return null;
            }

            IotCmdNbiEntity nbi = this.getNbi(deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), key.toString());
            if (nbi == null) {
                return null;
            }

            OperateManualTaskEntity taskEntity = this.getTask(deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), key.toString());
            if (taskEntity == null) {
                return null;
            }

            Map<String, Object> taskParam = taskEntity.getTaskParam().get(0);
            String operateMode = (String) taskParam.get("operateMode");
            String operateName = (String) taskParam.get("operateName");
            Integer timeout = (Integer) taskParam.get("timeout");
            Map<String, Object> param = (Map<String, Object>) taskParam.get("param");

            // 转换参数
            Map<String, Object> operateParam = this.buildOperateParam(nbi, param, value);


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
        if ("int".equalsIgnoreCase(dataType)) {
            value = Integer.parseInt(value.toString());
        }
        if ("long".equalsIgnoreCase(dataType)) {
            value = Long.parseLong(value.toString());
        }
        if ("bool".equalsIgnoreCase(dataType)) {
            value = Boolean.parseBoolean(value.toString());
        }
        if ("string".equalsIgnoreCase(dataType)) {
            value = value.toString();
        }

        return value;
    }

    private Map<String, Object> buildOperateParam(IotCmdNbiEntity nbi, Map<String, Object> param, Object value) throws UnsupportedEncodingException {
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

    /**
     * 查询NBI的配置信息
     *
     * @param manufacturer 厂商
     * @param deviceType   设备类型
     * @param paraKey      云平台定义的参数key
     * @return 云端的配置信息NBI
     */
    private IotCmdNbiEntity getNbi(String manufacturer, String deviceType, String paraKey) {
        return (IotCmdNbiEntity) MapUtils.getValue(this.cmdMap, manufacturer, deviceType, paraKey, "nbi");
    }

    /**
     * 查询SBI的配置信息
     *
     * @param manufacturer 厂商
     * @param deviceType   设备类型
     * @param paraKey      云平台定义的参数key
     * @return Fox-Edge的OperateManualTaskEntity中的Param格式信息
     */
    private OperateManualTaskEntity getTask(String manufacturer, String deviceType, String paraKey) {
        return (OperateManualTaskEntity) MapUtils.getValue(this.cmdMap, manufacturer, deviceType, paraKey, "task");
    }
}
