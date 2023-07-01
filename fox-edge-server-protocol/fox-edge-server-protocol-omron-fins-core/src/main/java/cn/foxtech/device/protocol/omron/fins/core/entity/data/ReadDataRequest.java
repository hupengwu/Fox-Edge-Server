package cn.foxtech.device.protocol.omron.fins.core.entity.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ReadDataRequest {
    /**
     * 发送：区域：1字节
     */
    private int area = AreaType.DM_Word;
    /**
     * 发送：字地址：2字节
     */
    private int wordAddress = 0;
    /**
     * 发送：位地址：1字节
     */
    private int bitAddress = 0;
    /**
     * 发送：数量数量为1时，代表两个字节
     */
    private int count = 0;
}
