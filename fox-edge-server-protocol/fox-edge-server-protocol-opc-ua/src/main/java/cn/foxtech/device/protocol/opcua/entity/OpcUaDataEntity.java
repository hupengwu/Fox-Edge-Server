package cn.foxtech.device.protocol.opcua.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaDataEntity {
    /**
     * 对象名称：用户自定义的名称，在设备内部唯一
     */
    private String objectName;

    /**
     * 节点ID：opc设备侧的节点ID，它在设备内具有唯一性
     */
    private OpcUaNodeId nodeId;

    /**
     * 节点名称：opc设备侧的节点名，实际上它只在同一个父亲节点下名称唯一，但是在设备中可不唯一，它更多的是作为描述信息
     */
    private String nodeName;

    /**
     * 数据类型：opc设备侧的节点数值的数据类型
     */
    private String type;
}
