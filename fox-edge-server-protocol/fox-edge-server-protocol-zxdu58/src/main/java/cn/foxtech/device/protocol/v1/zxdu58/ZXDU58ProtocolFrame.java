/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.device.protocol.v1.zxdu58;


import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ZXDU58的协议框架是电信总局的子协议
 * 在解码器的jar包被fox-edge-server-device-adapter服务扫描加载时，本JAR包在发布时
 * 因基类TelecomProtocol在另一个包fox-edge-server-protocol-common.jar里，
 * 所有要把这个jar包带上一起发布，否则在扫描阶段说找不到类，实际上是缺TelecomProtocol
 */
public class ZXDU58ProtocolFrame extends PduEntity {
    /**
     * @param param
     * @return
     */
    public static byte[] packCmd(Map<String, Object> param) {
        return packCmd4Map(param);
    }

    /**
     * ZX的电源自己会在结尾处额外再加一个0X0D，所以要重载处理
     *
     * @param arrCmd
     * @return
     */
    public static Map<String, Object> unPackCmd4Map(byte[] arrCmd) {
        if (arrCmd.length < 2) {
            return null;
        }

        // ZX的电源自己会在结尾处额外再加一个0X0D
        if (arrCmd[arrCmd.length - 2] == 0x0D && arrCmd[arrCmd.length - 1] == 0x0D) {
            arrCmd = Arrays.copyOf(arrCmd, arrCmd.length - 1);
        }

        return unPackCmd2Map(arrCmd);
    }

    /**
     * 如果没有夹带版本和地址参数，那么填写默认参数
     *
     * @param param
     */
    public static void pretreatParam(Map<String, Object> param) {
        if (!param.containsKey("VER")) {
            param.put("VER", 0x20);
        }
        if (!param.containsKey("ADR")) {
            param.put("ADR", 0x01);
        }
    }

    public static boolean checkParam(Map<String, Object> param) {
        if (!param.containsKey("VER") || !(param.get("VER") instanceof Byte || param.get("VER") instanceof Integer)) {
            return false;
        }

        if (!param.containsKey("ADR") || !(param.get("ADR") instanceof Byte || param.get("ADR") instanceof Integer)) {
            return false;
        }

        if (!param.containsKey("CID1") || !(param.get("CID1") instanceof Byte || param.get("CID1") instanceof Integer)) {
            return false;
        }

        if (!param.containsKey("CID2") || !(param.get("CID2") instanceof Byte || param.get("CID2") instanceof Integer)) {
            return false;
        }

        return param.containsKey("INFO") && param.get("INFO") instanceof byte[];
    }
    
    public static byte[] packCmd4Map(Map<String, Object> param) {
        // 检查参数是否完备
        if (!checkParam(param)) {
            return null;
        }


        PduEntity entity = new PduEntity();
        entity.setVer(Integer.decode(param.get("VER").toString()).byteValue());
        entity.setAddr(Integer.decode(param.get("ADR").toString()).byteValue());
        entity.setCid1(Integer.decode(param.get("CID1").toString()).byteValue());
        entity.setCid2(Integer.decode(param.get("CID2").toString()).byteValue());
        entity.setData((byte[]) param.get("INFO"));

        return PduEntity.encodePdu(entity);
    }

    public static Map<String, Object> unPackCmd2Map(byte[] arrCmd) {
        Map<String, Object> value = new HashMap<>();

        PduEntity entity = PduEntity.decodePdu(arrCmd);


        value.put("VER", entity.getVer());
        value.put("ADR", entity.getAddr());
        value.put("CID1", entity.getCid1());
        value.put("CID2", entity.getCid2());
        value.put("INFO", entity.getData());
        return value;
    }
}
