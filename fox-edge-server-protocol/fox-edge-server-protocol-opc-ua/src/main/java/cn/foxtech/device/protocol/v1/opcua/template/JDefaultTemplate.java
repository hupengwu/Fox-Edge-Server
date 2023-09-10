package cn.foxtech.device.protocol.v1.opcua.template;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.opcua.entity.OpcUaNodeId;
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
 * 列名称格式：object_name	node_id_namespace	node_id_identifier	value_type	remark
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
        Map<OpcUaNodeId, JDecoderValueParam> nodeMap = new HashMap<>();
        for (JDecoderValueParam jDecoderValueParam : rows) {
            if (MethodUtils.hasEmpty(jDecoderValueParam.object_name, jDecoderValueParam.node_id_namespace, jDecoderValueParam.node_id_identifier)) {
                continue;
            }

            // 构造出nodeId
            OpcUaNodeId nodeId = new OpcUaNodeId();
            nodeId.setNamespace(jDecoderValueParam.node_id_namespace);
            nodeId.setIdentifier(jDecoderValueParam.node_id_identifier);

            nameMap.put(jDecoderValueParam.getObject_name(), jDecoderValueParam);
            nodeMap.put(nodeId, jDecoderValueParam);
        }

        this.operate.decoderParam.nameMap = nameMap;
        this.operate.decoderParam.nodeMap = nodeMap;
        this.operate.decoderParam.table = table;
    }

    public OpcUaNodeId encodeNodeId(String objectName) {
        JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.nameMap.get(objectName);
        if (jDecoderValueParam == null) {
            throw new ProtocolException("csv中未定义该对象的信息:" + objectName);
        }

        // 构造成nodeId
        OpcUaNodeId nodeId = new OpcUaNodeId();
        nodeId.setNamespace(jDecoderValueParam.node_id_namespace);
        nodeId.setIdentifier(jDecoderValueParam.node_id_identifier);


        return nodeId;
    }

    public List<OpcUaNodeId> encodeNodeIdList(List<String> objectNameList) {
        List<OpcUaNodeId> nodeIdList = new ArrayList<>();
        for (String objectName : objectNameList) {
            JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.nameMap.get(objectName);
            if (jDecoderValueParam == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + objectName);
            }

            // 构造成nodeId
            OpcUaNodeId nodeId = new OpcUaNodeId();
            nodeId.setNamespace(jDecoderValueParam.node_id_namespace);
            nodeId.setIdentifier(jDecoderValueParam.node_id_identifier);

            nodeIdList.add(nodeId);
        }


        return nodeIdList;
    }

    public String getObjectName(OpcUaNodeId nodeId) {
        JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.nodeMap.get(nodeId);
        if (jDecoderValueParam == null) {
            return null;
        }

        return jDecoderValueParam.object_name;
    }

    public Map<String, Object> decodeValue(Map<String, Object> nodeId2Value) {
        Map<String, Object> result = new HashMap<>();

        for (String nodeId : nodeId2Value.keySet()) {
            Object value = nodeId2Value.get(nodeId);

            JDecoderValueParam jDecoderValueParam = this.operate.decoderParam.nodeMap.get(nodeId);
            if (jDecoderValueParam == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + nodeId);
            }

            result.put(jDecoderValueParam.getObject_name(), value);
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
        private Map<OpcUaNodeId, JDecoderValueParam> nodeMap = new HashMap<>();
    }


    /**
     * 代表一行记录
     */
    @Data
    static public class JDecoderValueParam implements Serializable {
        private String object_name;

        private Integer node_id_namespace;

        private String node_id_identifier;

        private String remark;
    }
}
