/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.hj212_2017.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.hj212_2017.utils.Crc16Util;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PduEntity {
    /**
     * 请求编码
     */
    private String qn = "";
    /**
     * 系统编码
     */
    private String st = "";
    /**
     * 命令编码
     */
    private String cn = "";
    /**
     * 访问密码
     */
    private String pw = "";
    /**
     * 设备唯一标识符
     */
    private String mn = "";
    /**
     * 标志位
     */
    private String flag = "";
    /**
     * 总包数：在分包的时候，才有该数据
     */
    private String pNum = "";
    /**
     * 分包号：在分包的时候，才有该数据
     */
    private String pNo = "";

    /**
     * 指令参数
     */
    private Map<String, Object> cp;


    public static String encodePdu(PduEntity entity) {
        String header = encodeHeader(entity);
        String cmdPar = encodeCmdPar(entity.cp);


        StringBuilder sb = new StringBuilder();
        sb.append(header);
        if (!cmdPar.isEmpty()) {
            sb.append("CP=&&");
            sb.append(cmdPar);
            sb.append("&&");
        }
        String body = sb.toString();
        int crc16 = Crc16Util.getCrc16(body.getBytes());

        // 包头
        sb = new StringBuilder();
        sb.append("##");
        sb.append(String.format("%04d", body.length()));
        sb.append(body);
        sb.append(String.format("%2X", (crc16 >> 8) & 0xff));
        sb.append(String.format("%2X", (crc16 >> 0) & 0xff));
        sb.append("\r\n");

        return sb.toString();
    }

    private static String encodeHeader(PduEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (!MethodUtils.hasEmpty(entity.qn)) {
            sb.append("QN=");
            sb.append(entity.qn);
            sb.append(";");
        }
        if (!MethodUtils.hasEmpty(entity.cn)) {
            sb.append("CN=");
            sb.append(entity.cn);
            sb.append(";");
        }
        if (!MethodUtils.hasEmpty(entity.pw)) {
            sb.append("PW=");
            sb.append(entity.pw);
            sb.append(";");
        }
        if (!MethodUtils.hasEmpty(entity.mn)) {
            sb.append("MN=");
            sb.append(entity.mn);
            sb.append(";");
        }
        if (!MethodUtils.hasEmpty(entity.st)) {
            sb.append("ST=");
            sb.append(entity.st);
            sb.append(";");
        }
        if (!MethodUtils.hasEmpty(entity.flag)) {
            sb.append("Flag=");
            sb.append(entity.flag);
            sb.append(";");
        }
        if (!MethodUtils.hasEmpty(entity.pNum)) {
            sb.append("PNUM=");
            sb.append(entity.pNum);
            sb.append(";");
        }
        if (!MethodUtils.hasEmpty(entity.pNo)) {
            sb.append("PNO=");
            sb.append(entity.pNo);
            sb.append(";");
        }


        return sb.toString();
    }

    private static void decodeHeader(String header, PduEntity pduEntity) {
        // 根据分隔符号;分拆数据
        String[] heads = header.split(";");

        for (String item : heads) {
            if (item.startsWith("QN=")) {
                pduEntity.qn = item.substring("QN=".length());
                continue;
            }
            if (item.startsWith("CN=")) {
                pduEntity.cn = item.substring("CN=".length());
                continue;
            }
            if (item.startsWith("PW=")) {
                pduEntity.pw = item.substring("PW=".length());
                continue;
            }
            if (item.startsWith("MN=")) {
                pduEntity.mn = item.substring("MN=".length());
                continue;
            }
            if (item.startsWith("ST=")) {
                pduEntity.st = item.substring("ST=".length());
                continue;
            }
            if (item.startsWith("Flag=")) {
                pduEntity.flag = item.substring("Flag=".length());
                continue;
            }
            if (item.startsWith("PNUM=")) {
                pduEntity.pNum = item.substring("PNUM=".length());
                continue;
            }
            if (item.startsWith(" PNO=")) {
                pduEntity.pNo = item.substring("PNO=".length());
                continue;
            }
        }
    }

    private static String encodeCmdPar(Map<String, Object> cp) {
        if (cp == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String k1 : cp.keySet()) {
            Object v1 = cp.get(k1);
            if (v1 instanceof Map) {
                Map<String, Object> map1 = (Map<String, Object>) v1;
                for (String k2 : map1.keySet()) {
                    Object v2 = map1.get(k2);
                    sb.append(k1);
                    sb.append("-");
                    sb.append(k2);
                    sb.append("=");
                    sb.append(v2);
                    sb.append(",");
                }
                // 删除最后的,
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
            } else {
                sb.append(k1);
                sb.append("=");
                sb.append(v1);
            }
            sb.append(";");
        }

        // 删除最后的;
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();

    }

    private static Map<String, Object> decodeCmdPar(String cmdPar) {
        // 根据分隔符号;分拆数据
        String[] paras = cmdPar.split(";");

        Map<String, Object> cp = new HashMap<>();
        for (String item : paras) {
            String[] pars = item.split(",");
            for (String par : pars) {
                String[] it = par.split("=");
                if (it.length < 2) {
                    continue;
                }

                String key = it[0];
                String value = it[1];

                // 检查：是否为单级数据
                if (pars.length == 1) {
                    cp.put(key, value);
                } else {
                    String[] ks = key.split("-");
                    if (ks.length < 2) {
                        continue;
                    }

                    String k = ks[0];
                    String v = ks[1];

                    Map<String, Object> mapValue = (Map<String, Object>) cp.computeIfAbsent(k, vv -> new HashMap<>());
                    mapValue.put(v, value);
                }
            }
        }

        return cp;

    }

    public static PduEntity decodePdu(String pdu) {
        // 拆解出数据段
        String data = decodeData(pdu);

        // 分拆头部区和参数区
        String header = "";
        String cmdPar = "";
        int start = data.indexOf("CP=&&");
        if (start >= 0) {
            int end = data.indexOf("&&", start + "CP=&&".length());
            cmdPar = data.substring(start + "CP=&&".length(), end);
            header = data.substring(0, start);
        } else {
            header = data;
        }

        PduEntity pduEntity = new PduEntity();

        // 头部数据
        decodeHeader(header, pduEntity);

        // 参数部分
        pduEntity.cp = decodeCmdPar(cmdPar);

        return pduEntity;
    }

    private static String decodeData(String pdu) {
        // 检查：包头
        if (!pdu.startsWith("##")) {
            throw new ProtocolException("包头必须为##");
        }
        // 检查：包尾
        if (!pdu.endsWith("\r\n")) {
            throw new ProtocolException("包尾必须为回车符和换行符");
        }
        // 简单验证：检查长度
        if (pdu.length() < 4 + 4) {
            throw new ProtocolException("报文长度小于8");
        }

        // 取出报文长度
        String strLen = pdu.substring(2, 6);
        int length = Integer.parseInt(strLen);

        // 检查：报文长度
        if (pdu.length() != length + 12) {
            throw new ProtocolException("报文长度不正确!");
        }

        // 数据段
        String data = pdu.substring(6, pdu.length() - 6);

        // 取出CRC
        String strCrc = pdu.substring(pdu.length() - 6, pdu.length() - 2);
        int crc16 = Integer.parseInt(strCrc, 16);
        if (Crc16Util.getCrc16(data.getBytes()) != crc16) {
            throw new ProtocolException("crc校验不正确!");
        }

        return data;
    }

}
