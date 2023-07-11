package cn.foxtech.channel.opcua.utils;

import cn.foxtech.channel.opcua.entity.OpcUaChannelEntity;
import cn.foxtech.channel.opcua.entity.OpcUaNodeId;
import cn.foxtech.channel.opcua.entity.OpcUaNodeTree;
import cn.foxtech.channel.opcua.enums.BrowseModeEnum;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import java.util.List;

public class BrowseNodeUtils {
    public static OpcUaNodeTree browseNode(OpcUaChannelEntity channelEntity, NodeId nodeId, String name, BrowseModeEnum browseMode) throws UaException {
        UaClient opcLink = channelEntity.getOpcLink();


        if (nodeId == null) {
            nodeId = Identifiers.ObjectsFolder;
        }

        OpcUaNodeTree opcUaNodeTree = new OpcUaNodeTree(OpcUaNodeId.buildEntity(nodeId));
        opcUaNodeTree.setName(name);

        List<? extends UaNode> nodes = opcLink.getAddressSpace().browseNodes(nodeId);
        for (UaNode node : nodes) {
            if (browseMode.equals(BrowseModeEnum.browseTree)) {
                opcUaNodeTree.addChildren(browseNode(channelEntity, node.getNodeId(), node.getBrowseName().getName(), browseMode));
            }
            if (browseMode.equals(BrowseModeEnum.browseChild)) {
                opcUaNodeTree.addChildren(node, null);
            }
            if (browseMode.equals(BrowseModeEnum.browseChildValue)) {
                DataValue dataValue = node.readAttribute(AttributeId.Value);
                Object value = ValueUtils.buildJsonValue(dataValue.getValue().getValue());
                opcUaNodeTree.addChildren(node, value);
            }
        }

        return opcUaNodeTree;
    }
}
