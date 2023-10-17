package cn.foxtech.channel.opcua.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaNodeId {
    /**
     * 名空间
     */
    private Integer namespace;
    /**
     * 名空间下的一个节点ID
     */
    private String identifier;

    public static OpcUaNodeId buildEntity(Integer namespace, String identifier) {
        if (namespace == null || identifier == null) {
            return null;
        }

        OpcUaNodeId result = new OpcUaNodeId();
        result.setNamespace(namespace);
        result.setIdentifier(identifier);
        return result;
    }

    public static OpcUaNodeId buildEntity(Map<String, Object> nodeIdMap) {
        if (nodeIdMap == null) {
            return null;
        }

        Integer namespace = (Integer) nodeIdMap.get("namespace");
        String identifier = (String) nodeIdMap.get("identifier");

        return buildEntity(namespace, identifier);
    }


    public static OpcUaNodeId buildEntity(NodeId nodeId) {
        if (nodeId == null) {
            return null;
        }

        OpcUaNodeId result = new OpcUaNodeId();
        result.setNamespace(nodeId.getNamespaceIndex().intValue());
        result.setIdentifier(nodeId.getIdentifier().toString());
        return result;
    }

    public NodeId buildNodeId() {
        return new NodeId(namespace, identifier);
    }
}
