package cn.foxtech.device.protocol.dlt645.core.entity;

import cn.foxtech.device.protocol.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DLT645v2007DataEntity extends DLT645DataEntity {
    /**
     * DI1/DI0
     */
    private byte di0 = 0;
    private byte di1 = 0;
    private byte di2 = 0;
    private byte di3 = 0;


    public String getKey() {
        String key = "";
        key += Integer.toString(this.di3, 16) + ":";
        key += Integer.toString(this.di2, 16) + ":";
        key += Integer.toString(this.di1, 16) + ":";
        key += Integer.toString(this.di0, 16) + "";
        return key.toUpperCase();
    }

    @Override
    public byte[] getDIn() {
        byte[] value = new byte[4];
        value[0] = this.di0;
        value[1] = this.di1;
        value[2] = this.di2;
        value[3] = this.di3;

        return value;
    }

    @Override
    public void setDIn(byte[] value) {
        if (value.length < 4) {
            throw new ProtocolException("数据长度小于4字节!");
        }

        this.di0 = value[0];
        this.di1 = value[1];
        this.di2 = value[2];
        this.di3 = value[3];
    }

    /**
     * 2007版的DIn 4字节
     *
     * @return
     */
    @Override
    public int getDInLen() {
        return 4;
    }
}
