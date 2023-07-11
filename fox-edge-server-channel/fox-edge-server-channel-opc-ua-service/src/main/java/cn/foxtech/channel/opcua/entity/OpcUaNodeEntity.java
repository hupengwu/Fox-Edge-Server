package cn.foxtech.channel.opcua.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaNodeEntity {
    /**
     * 节点ID
     */
    private OpcUaNodeId nodeId;
    /**
     * 数值
     */
    private Object value;
    /**
     * 数值类型
     */
    private String type;
}
