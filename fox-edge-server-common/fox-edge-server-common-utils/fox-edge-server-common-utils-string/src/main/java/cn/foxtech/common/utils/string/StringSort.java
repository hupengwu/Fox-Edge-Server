package cn.foxtech.common.utils.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringSort {
    public static String getMapString(Map data) {
        List keys = new ArrayList<>();
        keys.addAll(data.keySet());

        Collections.sort(keys, (o1, o2) -> {
            String str1 = toString(o1);
            String str2 = toString(o2);

            return str1.compareTo(str2);
        });

        StringBuilder str = new StringBuilder();
        str.append("[");
        for (Object key : keys) {
            Object value = data.get(key);

            str.append(key);
            str.append("=");

            if (value instanceof Map) {
                str.append(getMapString((Map) value));
            } else if (value instanceof List) {
                str.append(getListString((List) value));
            } else {
                str.append(value);
            }

            str.append(",");
        }

        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
        }
        str.append("]");

        return str.toString();
    }

    public static String getListString(List data) {
        Collections.sort(data, (o1, o2) -> {
            String str1 = toString(o1);
            String str2 = toString(o2);

            return str1.compareTo(str2);
        });

        StringBuilder str = new StringBuilder();
        for (Object value : data) {
            if (value instanceof Map) {
                str.append(getMapString((Map) value));
            } else if (value instanceof List) {
                str.append(getListString((List) value));
            } else {
                str.append(value);
            }

            str.append(",");
        }

        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

    private static String toString(Object value) {
        if (value instanceof Map) {
            return getMapString((Map) value);
        } else if (value instanceof List) {
            return getListString((List) value);
        } else {
            return value.toString();
        }
    }
}
