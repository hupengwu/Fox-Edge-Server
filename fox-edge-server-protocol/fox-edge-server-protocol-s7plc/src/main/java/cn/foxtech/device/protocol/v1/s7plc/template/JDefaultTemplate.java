/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.s7plc.template;

import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认格式的模板
 * 列名称格式：value_name	oid	value_type	remark
 */
@Data
public class JDefaultTemplate implements ITemplate {
    public static final String FORMAT_NAME = "default";

    private JDecoderParam decoderParam = new JDecoderParam();

    public String getSysTemplateName() {
        return FORMAT_NAME;
    }


    public void loadJsnModel(String modelName) {
        // 从进程的上下文中，获得设备模型信息
        Map<String, Object> deviceTemplateEntity = ApplicationContext.getDeviceModels(modelName);

        // 检测：上下文侧的时间戳和当前模型的时间戳是否一致
        Object updateTime = deviceTemplateEntity.getOrDefault("updateTime", 0L);
        if (this.decoderParam.updateTime.equals(updateTime)) {
            return;
        }

        // 取出JSON模型的数据列表
        Map<String, Object> modelParam = (Map<String, Object>) deviceTemplateEntity.getOrDefault("modelParam", new HashMap<>());
        List<Map<String, Object>> rows = (List<Map<String, Object>>) modelParam.getOrDefault("list", new ArrayList<>());

        // 将文件记录组织到map中
        Map<String, JDecoderValueParam> nameMap = new HashMap<>();
        Map<String, JDecoderValueParam> idMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            JDecoderValueParam jDecoderValueParam = new JDecoderValueParam();
            jDecoderValueParam.setValue_name((String) row.get("value_name"));
            jDecoderValueParam.setData_area((String) row.get("data_area"));
            jDecoderValueParam.setData_type((String) row.get("data_type"));
            jDecoderValueParam.setByte_index((String) row.get("byte_index"));
            jDecoderValueParam.setBit_index((String) row.get("bit_index"));
            jDecoderValueParam.setOid((String) row.get("oid"));
            jDecoderValueParam.setCount((String) row.get("count"));
            jDecoderValueParam.setRemark((String) row.get("remark"));


            // 检查：必填项目
            if (MethodUtils.hasEmpty(jDecoderValueParam.value_name,// 对象名称
                    jDecoderValueParam.data_area,// 数据区域
                    jDecoderValueParam.byte_index,// 字节索引
                    jDecoderValueParam.data_type// 数据类型
            )) {
                continue;
            }

            // 强制转大写
            jDecoderValueParam.data_type = jDecoderValueParam.data_type.toUpperCase();

            // 检查：字节索引是否为整数
            if (NumberUtils.parseLong(jDecoderValueParam.byte_index) == null) {
                continue;
            }
            // 检查：如果用户填写了参数，却不是整数
            if (!MethodUtils.hasEmpty(jDecoderValueParam.byte_index) && NumberUtils.parseLong(jDecoderValueParam.byte_index) == null) {
                continue;
            }
            if (!MethodUtils.hasEmpty(jDecoderValueParam.bit_index) && NumberUtils.parseLong(jDecoderValueParam.bit_index) == null) {
                continue;
            }

            Long count = NumberUtils.parseLong(jDecoderValueParam.count);

            if (jDecoderValueParam.data_type.equals("BYTE") || jDecoderValueParam.data_type.equals("STRING")) {
                if (count == null) {
                    continue;
                }
            } else {
                count = 1L;
            }
            jDecoderValueParam.count = count.toString();

            // 对象的ID
            jDecoderValueParam.oid = jDecoderValueParam.data_area + "." + jDecoderValueParam.byte_index;
            if (!MethodUtils.hasEmpty(jDecoderValueParam.bit_index)) {
                jDecoderValueParam.oid += "." + jDecoderValueParam.bit_index;
            }

            nameMap.put(jDecoderValueParam.getValue_name(), jDecoderValueParam);
            idMap.put(jDecoderValueParam.oid, jDecoderValueParam);
        }

        this.decoderParam.nameMap = nameMap;
        this.decoderParam.idMap = idMap;
        this.decoderParam.table = modelName;
        this.decoderParam.updateTime = updateTime;
    }


    public List<Map<String, Object>> encodeReadObjects(List<String> objectNameList) {
        List<Map<String, Object>> objects = new ArrayList<>();
        for (String objectName : objectNameList) {
            JDecoderValueParam jDecoderValueParam = this.decoderParam.nameMap.get(objectName);
            if (jDecoderValueParam == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + objectName);
            }

            Map<String, Object> param = new HashMap<>();
            param.put("address", jDecoderValueParam.oid);
            param.put("dataType", jDecoderValueParam.data_type);
            param.put("count", Integer.valueOf(jDecoderValueParam.count));

            objects.add(param);
        }


        return objects;
    }

    public List<Map<String, Object>> encodeWriteObjects(Map<String, Object> values) {
        List<Map<String, Object>> objects = new ArrayList<>();
        for (String name : values.keySet()) {
            Object value = values.get(name);

            JDecoderValueParam jDecoderValueParam = this.decoderParam.nameMap.get(name);
            if (jDecoderValueParam == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + name);
            }

            Map<String, Object> param = new HashMap<>();
            param.put("address", jDecoderValueParam.oid);
            param.put("dataType", jDecoderValueParam.data_type);
            param.put("value", value);

            objects.add(param);
        }


        return objects;
    }

    public Map<String, Object> decodeReadValue(List<Map<String, Object>> values) {
        Map<String, Object> result = new HashMap<>();

        for (Map<String, Object> map : values) {
            // 对象的ID
            String oid = (String) map.get("address");
            Object value = map.get("value");


            JDecoderValueParam jDecoderValueParam = this.decoderParam.idMap.get(oid);
            if (jDecoderValueParam.oid == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + oid);
            }

            try {
                result.put(jDecoderValueParam.getValue_name(), value);
            } catch (Exception e) {
                throw new ProtocolException("数据转换错误：" + jDecoderValueParam.value_name + " 文本为：" + value);
            }
        }

        return result;
    }


    @Data
    static public class JEncoderParam implements Serializable {
        private String name;
    }

    /**
     * 代表一页记录
     */
    @Data
    static public class JDecoderParam implements Serializable {
        private String table;
        private Object updateTime = 0L;
        private Map<String, JDecoderValueParam> nameMap = new HashMap<>();
        private Map<String, JDecoderValueParam> idMap = new HashMap<>();
    }


    /**
     * 代表一行记录
     */
    @Data
    static public class JDecoderValueParam implements Serializable {
        /**
         * 对象的名称
         */
        private String value_name;
        /**
         * 数据区域：如DB1、DB2、I0、Q0、M0、V0
         */
        private String data_area;
        /**
         * 字节索引：必填项
         */
        private String byte_index;
        /**
         * 位索引：可填项目，当为0时，可不填
         */
        private String bit_index;
        /**
         * 数据类型：例如FLOAT64、BYTE、STRING等代码中定义的数据类型标识
         */
        private String data_type;
        /**
         * 数量：只有BYTE、STRING两种类型才有效
         */
        private String count;
        /**
         * 描述
         */
        private String remark;
        /**
         * 对象的ID：该字段不是用户输入的，是根据用户输入的其他字段，内部计算出来的ID
         */
        private String oid;
    }
}
