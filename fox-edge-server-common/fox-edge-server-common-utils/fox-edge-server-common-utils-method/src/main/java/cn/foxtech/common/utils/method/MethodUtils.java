package cn.foxtech.common.utils.method;


import java.util.Collection;
import java.util.Map;

public class MethodUtils {
    /**
     * 是否包含null或者empty的参数
     *
     * @param paramList
     * @return
     */
    public static boolean hasEmpty(Map<String, Object> map, Object... paramList) {
        for (Object key : paramList) {
            if (isEmpty(map.get(key))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isEmpty(Object param) {
        if (param == null) {
            return true;
        }

        // 检查：常用的类型是否为isEmpty：String/Map/
        if (param instanceof String && ((String) (param)).isEmpty()) {
            return true;
        }
        if (param instanceof Map && ((Map) (param)).isEmpty()) {
            return true;
        }
        return param instanceof Collection && ((Collection) (param)).isEmpty();
    }

    /**
     * 是否包含null或者empty的参数
     *
     * @param paramList
     * @return
     */
    public static boolean hasEmpty(Object... paramList) {
        for (Object param : paramList) {
            if (isEmpty(param)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否包含null的参数
     *
     * @param paramList
     * @return
     */
    public static boolean hasNull(Object... paramList) {
        for (Object param : paramList) {
            if (param == null) {
                return true;
            }
        }

        return false;
    }
}

