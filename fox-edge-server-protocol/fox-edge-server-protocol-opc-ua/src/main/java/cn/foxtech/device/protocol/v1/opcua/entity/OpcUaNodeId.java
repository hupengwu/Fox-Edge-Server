package cn.foxtech.device.protocol.v1.opcua.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpcUaNodeId nodeId = (OpcUaNodeId) o;
        return Objects.equals(namespace, nodeId.namespace) && Objects.equals(identifier, nodeId.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, identifier);
    }

    @Override
    public String toString() {
        return "NodeId:[" + namespace + ":" + identifier + "]";
    }
}
