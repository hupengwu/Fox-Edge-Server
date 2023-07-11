package cn.foxtech.channel.opcua.enums;

public enum BrowseModeEnum {
    /**
     * 读取树节点信息的最简信息：nodeId、nodeName、children
     */
    browseTree(1, "browseTree"),
    /**
     * 读取下一层的最简信息：nodeId、nodeName
     */
    browseChild(2, "browseChild"),
    /**
     * 读取树节点的数值：nodeId、nodeName、nodeValue
     */
    browseChildValue(3, "browseChildValue");

    private final Integer type;
    private final String desc;

    BrowseModeEnum(Integer type, String desc) {
        this.desc = desc;
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
