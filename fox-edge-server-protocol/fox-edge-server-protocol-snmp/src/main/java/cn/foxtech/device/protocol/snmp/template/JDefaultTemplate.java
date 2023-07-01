package cn.foxtech.device.protocol.snmp.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.template.ITemplate;
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
        Map<String, JDecoderValueParam> oidMap = new HashMap<>();
        for (JDecoderValueParam jDecoderValueParam : rows) {
            nameMap.put(jDecoderValueParam.getValue_name(), jDecoderValueParam);
            oidMap.put(jDecoderValueParam.getOid(), jDecoderValueParam);
        }

        this.operate.decoderParam.nameMap = nameMap;
        this.operate.decoderParam.oidMap = oidMap;
        this.operate.decoderParam.table = table;
    }


    public List<String> encodeOIDList(List<String> objectNameList) {
        List<String> oidList = new ArrayList<>();
        for (String objectName : objectNameList) {
            JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.nameMap.get(objectName);
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

            JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.oidMap.get(oid);
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
