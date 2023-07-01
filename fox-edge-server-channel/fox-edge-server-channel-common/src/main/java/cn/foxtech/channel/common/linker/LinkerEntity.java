package cn.foxtech.channel.common.linker;

import lombok.Data;

/**
 * 桥接链路实体
 */
@Data
public class LinkerEntity {
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 是否建立桥接链路
     */
    private boolean linked = false;
    /**
     * 心跳成功的最近时间
     */
    private long lastActive = 0;

    /**
     * 心跳失败的连续次数
     */
    private long failActive = 0;
}
