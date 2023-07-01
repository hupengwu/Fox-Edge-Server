package cn.foxtech.device.protocol.omron.fins.core.entity.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class WriteDataRequest {
    /**
     * 发送：区域：1字节
     */
    private int area = AreaType.DM_Word;
    /**
     * 发送：偏移量：2字节，高位在前，低位在后
     */
    private int wordAddress = 0;
    /**
     * 发送：位地址：1字节
     */
    private int bitAddress = 0;
    /**
     * 发送：一次只能写2字节
     */
    private byte[] data = new byte[2];
}
