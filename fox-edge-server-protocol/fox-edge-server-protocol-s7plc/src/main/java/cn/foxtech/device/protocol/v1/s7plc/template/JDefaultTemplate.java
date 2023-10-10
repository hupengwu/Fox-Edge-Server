package cn.foxtech.device.protocol.v1.s7plc.template;

import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.Data;

import java.io.File;
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

    private JOperate operate = new JOperate();

    public String getSysTemplateName() {
        return FORMAT_NAME;
    }

    public void setUserTemplateInfo(String userTemplateName, String tableName) {

    }

    /**
     * 从CSV文件中装载映射表
     *
     * @param table csv表名称
     */
    public void loadCsvFile(String table) {
        File dir = new File("");

        File file = new File(dir.getAbsolutePath() + "/template/" + table);
        CsvReader csvReader = CsvUtil.getReader();
        List<JDecoderValueParam> rows = csvReader.read(ResourceUtil.getReader(file.getPath(), CharsetUtil.CHARSET_GBK), JDecoderValueParam.class);

        // 将文件记录组织到map中
        Map<String, JDecoderValueParam> nameMap = new HashMap<>();
        Map<String, JDecoderValueParam> idMap = new HashMap<>();
        for (JDecoderValueParam jDecoderValueParam : rows) {
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

        this.operate.decoderParam.nameMap = nameMap;
        this.operate.decoderParam.idMap = idMap;
        this.operate.decoderParam.table = table;
    }


    public List<Map<String, Object>> encodeReadObjects(List<String> objectNameList) {
        List<Map<String, Object>> objects = new ArrayList<>();
        for (String objectName : objectNameList) {
            JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.nameMap.get(objectName);
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

            JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.nameMap.get(name);
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


            JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.idMap.get(oid);
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

    /**
     * 代表一个交互操作
     */
    @Data
    static public class JOperate implements Serializable {
        private String name = "";
        private String operateName = "";
        private JEncoderParam encoderParam = new JEncoderParam();
        private JDecoderParam decoderParam = new JDecoderParam();
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
