package cn.foxtech.common.utils.number;

public class NumberUtils {
    /**
     * 将数字类型的对象，转换为Long类型
     *
     * @param object
     * @return
     */
    public static Long makeLong(Object object) {
        if (object instanceof Long) {
            return (Long) object;
        }
        if (object instanceof Integer) {
            return ((Integer) object).longValue();
        }
        if (object instanceof Short) {
            return ((Short) object).longValue();
        }

        return null;
    }

    public static Long parseLong(String object) {
        try {
            return Long.parseLong(object);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer makeInteger(Object object) {
        if (object instanceof Long) {
            return ((Long) object).intValue();
        }
        if (object instanceof Integer) {
            return (Integer) object;
        }
        if (object instanceof Short) {
            return ((Short) object).intValue();
        }

        return null;
    }
}
