package cn.foxtech.link.tcp2tcp.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tcp2TcpLinkEntity {
    // 北向参数
    private String serverHost;
    private Integer serverPort;

    // 南向参数
    private String remoteHost;
    private Integer remotePort;
}
