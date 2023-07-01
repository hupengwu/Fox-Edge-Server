package cn.foxtech.device.protocol.cjt188.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * CJT188的地址框架
 * 广播地址：AA AA AA AA AA AA AA
 *
 * 对于未知地址和表类型的数据，可以在一对一连接的时候，用广播地址和广播类型的方式读取。
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CJT188Address {
    /**
     * 14字节长度的字符串
     */
    private String value = "AA AA AA AA AA AA AA";

    /**
     * 同批次的生产流水号：6位数
     */
    public String getSerialNumber() {
        if (value == null || value.length() < 14) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(value.charAt(4));
        sb.append(value.charAt(5));
        sb.append(value.charAt(2));
        sb.append(value.charAt(3));
        sb.append(value.charAt(0));
        sb.append(value.charAt(1));
        return sb.toString();
    }

    /**
     * 生产批次：年月
     */
    public String getProductionTime() {
        if (value == null || value.length() < 14) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(value.charAt(8));
        sb.append(value.charAt(9));
        sb.append(value.charAt(6));
        sb.append(value.charAt(7));
        return sb.toString();
    }

    /**
     * 生产厂商代码：4位数
     */
    public String getManufacturerCode() {
        if (value == null || value.length() < 14) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(value.charAt(12));
        sb.append(value.charAt(13));
        sb.append(value.charAt(10));
        sb.append(value.charAt(11));
        return sb.toString();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("生产企业=" + this.getManufacturerCode() + ",");
        sb.append("生产批次=" + this.getProductionTime() + ",");
        sb.append("生产序号=" + this.getSerialNumber());
        return sb.toString();
    }
}
