package cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.infobj.object;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.ByteUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class AddrObject {
    /**
     * IP地址（N 字节）：填充个时，以分号结尾的IP，例如"192.168.1.9;"
     */
    private String ip = "";
    /**
     * 用户名（N 字节）
     */
    private String userName = "";
    /**
     * 密码（N 字节）
     */
    private String password = "";
    /**
     * 文件路径（N 字节）
     */
    private String filePath = "";
    /**
     * 文件名称（N 字节）
     */
    private String fileName = "";

    /**
     * 地址预留(1 字节)
     */
    private int reserve = 0;

    public static int getSize(byte[] data, int offset) {
        if (data.length < offset + 4) {
            throw new ProtocolException("地址对象的长度，最小为9");
        }

        int length = 0;
        length += data[offset + 0] & 0xff;
        length += data[offset + 1] & 0xff;
        length += data[offset + 2] & 0xff;

        // 其他字符（4 字节）
        length += 4;

        return length;
    }

    public int getSize() {
        String ip = this.ip.replaceAll(";", "") + ";";
        String filePath = this.filePath.replaceAll(";", "") + ";";
        String fileName = this.fileName.replaceAll(";", "") + ";";
        String userName = this.userName.replaceAll(";", "") + ";";
        String password = this.password.replaceAll(";", "") + ";";

        int length = 0;


        length += ip.length();
        length += filePath.length();
        length += fileName.length();
        if (length > 0x100) {
            throw new ProtocolException("地址对象中，IP+文件路径+文件名称的长度，超过了255");
        }

        length += userName.length();
        length += password.length();

        // 其他字符（4 字节）
        length += 4;

        return length;
    }

    public int decode(byte[] data, int offset) {
        if (data.length < offset + 4 + 5) {
            throw new ProtocolException("地址对象的长度，最小为9");
        }

        int index = 0;

        // 地址长度（1 字节）
        int len1 = data[offset + index] & 0xff;
        index += 1;

        // 用户名长度（1 字节）
        int len2 = data[offset + index] & 0xff;
        index += 1;

        // 用户密码长度（1 字节）
        int len3 = data[offset + index] & 0xff;
        index += 1;

        // 预留（1 字节）
        this.reserve = data[offset + index] & 0xff;
        index += 1;

        // 验证：长度是否正确
        if (data.length != offset + 4 + len1 + len2 + len3) {
            throw new ProtocolException("地址对象的长度，不正确!");
        }

        // IP地址，分号结尾（N 字节）
        String txt = ByteUtils.decodeAscii(data, offset + 4, len1 + len2 + len3, true);
        index += len1 + len2 + len3;

        // 根据;分拆字符串：不能用JAVA的txt.split("\\;");，它遇到";;;;;"，会分拆为空。
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (txt.charAt(i) != ';') {
                sb.append(c);
            } else {
                list.add(sb.toString());
            }
        }
        if (list.size() != 5) {
            throw new ProtocolException("地址对象的内容，不正确!");
        }


        this.ip = list.get(0);
        this.userName = list.get(1);
        this.password = list.get(2);
        this.filePath = list.get(3);
        this.fileName = list.get(4);

        return index;
    }

    public int encode(byte[] data, int offset) {
        String ip = this.ip.replaceAll(";", "") + ";";
        String filePath = this.filePath.replaceAll(";", "") + ";";
        String fileName = this.fileName.replaceAll(";", "") + ";";
        String userName = this.userName.replaceAll(";", "") + ";";
        String password = this.password.replaceAll(";", "") + ";";

        int index = 0;


        // 地址长度（1 字节）
        data[offset + index] = (byte) (ip.length() + filePath.length() + fileName.length());
        index += 1;

        // 用户名长度（1 字节）
        data[offset + index] = (byte) userName.length();
        index += 1;

        // 用户密码长度（1 字节）
        data[offset + index] = (byte) password.length();
        index += 1;

        // 预留（1 字节）
        data[offset + index] = (byte) this.reserve;
        index += 1;


        // IP地址，分号结尾（N 字节）
        ByteUtils.encodeAscii(ip, data, offset + index, ip.length(), true);
        index += ip.length();

        // 用户名称（N 字节）
        ByteUtils.encodeAscii(userName, data, offset + index, userName.length(), true);
        index += userName.length();

        // 用户密码（N 字节）
        ByteUtils.encodeAscii(password, data, offset + index, password.length(), true);
        index += password.length();

        // 文件路径（N 字节）
        ByteUtils.encodeAscii(filePath, data, offset + index, filePath.length(), true);
        index += filePath.length();

        // 文件名称（N 字节）
        ByteUtils.encodeAscii(fileName, data, offset + index, fileName.length(), true);
        index += fileName.length();

        return index;
    }
}
