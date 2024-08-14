/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.midea.dcm4.uitls;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;

import java.util.Map;

public class SettingsUtils {
    public static void decodeSettingsMode(byte value, Map<String, Object> result) {
        String key = "设定模式";
        int bitsValue = BitValueUtils.getBitsValue(value, 0, 3);
        switch (bitsValue) {
            case 0:
                result.put(key, "自动");
                break;
            case 1:
                result.put(key, "制冷");
                break;
            case 2:
                result.put(key, "制热");
                break;
            case 3:
                result.put(key, "只送风");
                break;
            case 4:
                result.put(key, "除湿");
                break;
        }

        key = "强劲功能";
        bitsValue = BitValueUtils.getBitsValue(value, 3, 1);
        switch (bitsValue) {
            case 0:
                result.put(key, "关闭");
                break;
            case 1:
                result.put(key, "打开");
                break;
        }

        key = "风速";
        bitsValue = BitValueUtils.getBitsValue(value, 5, 2);
        switch (bitsValue) {
            case 0:
                result.put(key, "自动风");
                break;
            case 1:
                result.put(key, "高风");
                break;
            case 2:
                result.put(key, "中风");
                break;
            case 3:
                result.put(key, "低风");
                break;
        }

        key = "空调开关";
        bitsValue = BitValueUtils.getBitsValue(value, 7, 1);
        switch (bitsValue) {
            case 0:
                result.put(key, "关机");
                break;
            case 1:
                result.put(key, "开机");
                break;
        }

    }

    public static int encodeSettingsMode(Map<String, Object> param) {
        int result = 0;

        int bitsValue = 0;
        Object par = param.get("设定模式");
        if ("自动".equals(par)) {
            bitsValue |= 0;
        } else if ("制冷".equals(par)) {
            bitsValue |= 1;
        } else if ("制热".equals(par)) {
            bitsValue |= 2;
        } else if ("只送风".equals(par)) {
            bitsValue |= 3;
        } else if ("除湿".equals(par)) {
            bitsValue |= 4;
        } else {
            throw new ProtocolException("参数缺失:设定模式");
        }
        result |= (bitsValue << 0);

        bitsValue = 0;
        par = param.get("强劲功能");
        if ("关闭".equals(par)) {
            bitsValue |= 0;
        } else if ("打开".equals(par)) {
            bitsValue |= 1;
        } else {
            throw new ProtocolException("参数缺失:强劲功能");
        }
        result |= (bitsValue << 3);


        bitsValue = 0;
        par = param.get("风速");
        if ("自动风".equals(par)) {
            bitsValue |= 0;
        } else if ("高风".equals(par)) {
            bitsValue |= 1;
        } else if ("中风".equals(par)) {
            bitsValue |= 2;
        } else if ("低风".equals(par)) {
            bitsValue |= 3;
        } else {
            throw new ProtocolException("参数缺失:风速");
        }
        result |= (bitsValue << 5);


        bitsValue = 0;
        par = param.get("空调开关");
        if ("关机".equals(par)) {
            bitsValue |= 0;
        } else if ("开机".equals(par)) {
            bitsValue |= 1;
        } else {
            throw new ProtocolException("参数缺失:空调开关");
        }
        result |= (bitsValue << 7);

        return result;
    }
}
