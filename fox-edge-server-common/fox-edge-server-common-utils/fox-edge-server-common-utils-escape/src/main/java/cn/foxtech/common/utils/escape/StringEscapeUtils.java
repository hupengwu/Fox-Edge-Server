/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.escape;

import java.util.Map;

public class StringEscapeUtils {
    /**
     * sql的注入：将sql中的相关的特殊字符，转义为外观看起来相似的汉字字符或者其他字符
     * 参考：https://blog.csdn.net/fastergohome/article/details/102554809
     */
    public static String escapeSql(String value) {
        if (value == null) {
            return value;
        }

        // 注释符#的注入攻击，将#替换成汉字的＃
        value = value.replaceAll("#", "＃");
        // 注释符--的注入攻击，将-- 替换成间隔的 - -
        value = value.replaceAll("--", " - - ");

        // 转义字符\字符的攻击,将\替换陈\\
        value = value.replace("\\", "\\\\");


        // 字符符号'的攻击，将字符'替换成汉字的‘
        value = value.replaceAll("'", "‘");
        // 字符串符“的攻击，将字符" 替换成汉字的“
        value = value.replaceAll("\"", "“");
        // 字符符号(的攻击，将字符'替换成汉字的（
        value = value.replaceAll("\\(", "（");
        // 字符符号(的攻击，将字符'替换成汉字的（
        value = value.replaceAll("\\)", "）");

        return value;
    }

    public static Map<String, Object> escapeSql(Map<String, Object> values) {
        if (values == null) {
            return values;
        }

        for (String key : values.keySet()) {
            Object value = values.get(key);
            if (value instanceof String) {
                values.put(key, escapeSql((String) value));
            }
        }


        return values;
    }
}
