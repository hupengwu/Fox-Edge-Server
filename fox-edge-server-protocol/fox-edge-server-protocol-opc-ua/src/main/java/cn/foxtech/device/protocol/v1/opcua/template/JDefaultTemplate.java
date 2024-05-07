package cn.foxtech.device.protocol.v1.opcua.template;

import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.opcua.entity.OpcUaNodeId;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import lombok.Data;

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
        Map<OpcUaNodeId, JDecoderValueParam> nodeMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            JDecoderValueParam jDecoderValueParam = new JDecoderValueParam();
            jDecoderValueParam.setObject_name((String) row.get("object_name"));
            jDecoderValueParam.setNode_id_identifier((String) row.get("node_id_identifier"));
            jDecoderValueParam.setNode_id_namespace(Integer.parseInt(row.get("node_id_namespace").toString()));
            jDecoderValueParam.setRemark((String) row.get("remark"));

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

        this.decoderParam.nameMap = nameMap;
        this.decoderParam.nodeMap = nodeMap;
        this.decoderParam.table = modelName;
        this.decoderParam.updateTime = updateTime;
    }

    public OpcUaNodeId encodeNodeId(String objectName) {
        JDecoderValueParam jDecoderValueParam = this.decoderParam.nameMap.get(objectName);
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
            JDecoderValueParam jDecoderValueParam = this.decoderParam.nameMap.get(objectName);
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
        JDecoderValueParam jDecoderValueParam = this.decoderParam.nodeMap.get(nodeId);
        if (jDecoderValueParam == null) {
            return null;
        }

        return jDecoderValueParam.object_name;
    }

    public Map<String, Object> decodeValue(Map<String, Object> nodeId2Value) {
        Map<String, Object> result = new HashMap<>();

        for (String nodeId : nodeId2Value.keySet()) {
            Object value = nodeId2Value.get(nodeId);

            JDecoderValueParam jDecoderValueParam = this.decoderParam.nodeMap.get(nodeId);
            if (jDecoderValueParam == null) {
                throw new ProtocolException("csv中未定义该对象的信息:" + nodeId);
            }

            result.put(jDecoderValueParam.getObject_name(), value);
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
