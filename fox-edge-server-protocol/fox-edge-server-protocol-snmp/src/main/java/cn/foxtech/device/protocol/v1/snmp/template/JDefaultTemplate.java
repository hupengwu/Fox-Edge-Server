/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.device.protocol.v1.snmp.template;

import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
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
        Map<String, JDecoderValueParam> oidMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            JDecoderValueParam jDecoderValueParam = new JDecoderValueParam();
            jDecoderValueParam.setValue_name((String) row.get("value_name"));
            jDecoderValueParam.setOid((String) row.get("oid"));
            jDecoderValueParam.setValue_type((String) row.get("value_type"));

            nameMap.put(jDecoderValueParam.getValue_name(), jDecoderValueParam);
            oidMap.put(jDecoderValueParam.getOid(), jDecoderValueParam);
        }

        this.decoderParam.nameMap = nameMap;
        this.decoderParam.oidMap = oidMap;
        this.decoderParam.table = modelName;
        this.decoderParam.updateTime = updateTime;
    }


    public List<String> encodeOIDList(List<String> objectNameList) {
        List<String> oidList = new ArrayList<>();
        for (String objectName : objectNameList) {
            JDecoderValueParam jDecoderValueParam = this.decoderParam.nameMap.get(objectName);
            if (jDecoderValueParam == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + objectName);
            }

            oidList.add(jDecoderValueParam.oid);
        }


        return oidList;
    }

    public Map<String, Object> decodeValue(Map<String, Object> oid2value) {
        Map<String, Object> result = new HashMap<>();

        for (String oid : oid2value.keySet()) {
            String value = (String) oid2value.get(oid);

            JDecoderValueParam jDecoderValueParam = this.decoderParam.oidMap.get(oid);
            if (jDecoderValueParam.oid == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + oid);
            }

            try {
                if ("String".equals(jDecoderValueParam.value_type)) {
                    result.put(jDecoderValueParam.getValue_name(), value);
                    continue;
                }
                if ("Integer".equals(jDecoderValueParam.value_type)) {
                    result.put(jDecoderValueParam.getValue_name(), Integer.valueOf(value));
                    continue;
                }
                if ("Long".equals(jDecoderValueParam.value_type)) {
                    result.put(jDecoderValueParam.getValue_name(), Long.valueOf(value));
                    continue;
                }
                if ("Float".equals(jDecoderValueParam.value_type)) {
                    result.put(jDecoderValueParam.getValue_name(), Float.valueOf(value));
                    continue;
                }
                if ("Double".equals(jDecoderValueParam.value_type)) {
                    result.put(jDecoderValueParam.getValue_name(), Double.valueOf(value));
                    continue;
                }
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
        private Map<String, JDecoderValueParam> oidMap = new HashMap<>();
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
         * 对象的oid
         */
        private String oid;
        /**
         * 数据类型
         * Hex-STRING：十六进制格式的字符串
         * Opaque：浮点数
         * INTEGER：整数
         * STRING：字符串格式
         */
        private String value_type;

        private String remark;
    }
}
