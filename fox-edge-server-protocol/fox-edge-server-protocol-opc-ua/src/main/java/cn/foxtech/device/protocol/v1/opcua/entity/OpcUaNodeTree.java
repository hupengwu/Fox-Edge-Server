package cn.foxtech.device.protocol.v1.opcua.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaNodeTree {
    /**
     * 节点名称：设备的命名，只是方便理解，并不具有ID的特性
     */
    private String name;
    /**
     * 节点ID：这个才是唯一性ID
     */
    private OpcUaNodeId nodeId;
    /**
     * 数值
     */
    private Object value;
    /**
     * 只节点
     */
    private List<OpcUaNodeTree> children = new ArrayList<>();

    public OpcUaNodeTree(OpcUaNodeId nodeId) {
        this.nodeId = nodeId;
    }
}