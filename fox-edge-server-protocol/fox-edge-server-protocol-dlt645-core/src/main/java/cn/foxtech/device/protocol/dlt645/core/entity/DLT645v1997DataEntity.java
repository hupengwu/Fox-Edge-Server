package cn.foxtech.device.protocol.dlt645.core.entity;

import cn.foxtech.device.protocol.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DLT645v1997DataEntity extends DLT645DataEntity {
    /**
     * DI1/DI0
     */
    private byte di0l = 0;
    private byte di0h = 0;
    private byte di1l = 0;
    private byte di1h = 0;

    @Override
    public String getKey() {
        String key = "";
        key += Integer.toString(this.di1h, 16) + ":";
        key += Integer.toString(this.di1l, 16) + ":";
        key += Integer.toString(this.di0h, 16) + ":";
        key += Integer.toString(this.di0l, 16) + "";
        return key.toUpperCase();
    }

    @Override
    public byte[] getDIn() {
        byte[] value = new byte[2];
        value[0] = (byte) (this.di0l + (this.di0h << 4));
        value[1] = (byte) (this.di1l + (this.di1h << 4));
        return value;
    }

    @Override
    public void setDIn(byte[] value) {
        if (value.length < 2) {
            throw new ProtocolException("数据长度小于2字节!");
        }

        // DI值
        this.di1h = (byte) ((value[1] & 0xf0) >> 4);
        this.di1l = (byte) (value[1] & 0x0f);
        this.di0h = (byte) ((value[0] & 0xf0) >> 4);
        this.di0l = (byte) (value[0] & 0x0f);
    }

    /**
     * 1997版的DIn2字节
     *
     * @return
     */
    @Override
    public int getDInLen() {
        return 2;
    }
}
