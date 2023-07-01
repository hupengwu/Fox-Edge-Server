package cn.foxtech.device.protocol.omron.fins.core.entity.pdu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 控制信息头：这部分数据需要用户根据自己的情况进行预配置
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Header {
    /**
     * 信息控制码
     */
    private int ICF = 0;
    /**
     * (Reserved)预留 一般为0x00
     */
    private int RSV = 0;
    /**
     * (Gateway count)网关数量，一般为0x02
     */
    private int GCT = 0;
    /**
     * (Destination network address)目标网络地址
     * 00：本地网络
     * 01 to 7F：远程网络
     */
    private int DNA = 0;
    /**
     * (Destination node number)目标节点号。
     * 01 to 7E：SYSMAC NET 网络节点号
     * 01 to 3E：SYSMAC LINK 网络节点号
     * FF：广播节点号
     */
    private int DA1 = 0;
    /**
     * (Source unit number)源单元号。
     * 00：PC(CPU)
     * FE：SYSMAC NET连接单元或者SYSMAC LINK单元连接网络
     * 10 to 1F：CPU 总线单元
     */
    private int DA2 = 0;
    /**
     * (Source network address)源网络地址。
     * 00：本地网络
     * 01 to 7F：远程网络
     */
    private int SNA = 0;
    /**
     * (Source node number)源节点号
     * 01 to 7E：SYSMAC NET 网络节点号
     * 01 to 3E：SYSMAC LINK 网络节点号
     * FF：广播节点号
     */
    private int SA1 = 0;
    /**
     * (Source Unit address)源单元地址
     * 00：PC(CPU)
     * FE：SYSMAC NET连接单元或者SYSMAC LINK单元连接网络
     * 10 to 1F：CPU 总线单元
     */
    private int SA2 = 0;
    /**
     * (Service ID) 序列号 范围00-FF
     */
    private int SID = 0;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ICF(信息控制码):");
        sb.append((this.ICF & 0x01) == 0 ? "需要响应" : "不需要响应");
        sb.append(",");
        sb.append((this.ICF & 0x40) == 0 ? "命令" : "响应");
        sb.append(",");
        sb.append((this.ICF & 0x80) == 0 ? "不使用" : "使用");
        sb.append(";");

        sb.append("RSV(预留):");
        sb.append(this.RSV);
        sb.append(";");

        sb.append("GCT(网关数量):");
        sb.append(this.GCT);
        sb.append(";");

        sb.append("DNA(目标网络地址):");
        sb.append(this.DNA == 0 ? "本地网络" : "远程网络" + this.DNA);
        sb.append(";");

        sb.append("DA1(目标节点号):");
        sb.append(this.DA1);
        sb.append(";");

        sb.append("DA2(源单元号):");
        sb.append(this.DA2 == 0 ? "PC" : this.DA2);
        sb.append(";");

        sb.append("SNA(源网络地址):");
        sb.append(this.SNA == 0 ? "本地网络" : "远程网络" + this.DNA);
        sb.append(";");

        sb.append("SA1(源节点号):");
        sb.append(this.SA1);
        sb.append(";");

        sb.append("SA2(源节点号):源单元地址");
        sb.append(this.SA2 == 0 ? "PC" : this.SA2);
        sb.append(";");

        sb.append("SID(源节点号):序列号");
        sb.append(this.SID);
        sb.append(";");

        return sb.toString();
    }
}
