package cn.foxtech.trigger.logic.simple.trigger;

import cn.foxtech.trigger.logic.common.FoxEdgeTriggerMethod;
import cn.foxtech.trigger.logic.common.FoxEdgeTriggerModel;
import cn.foxtech.trigger.logic.common.ObjectValue;
import cn.foxtech.core.exception.ServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FoxEdgeTriggerModel(name = "防抖触发器", manufacturer = "FoxTeam", description = "系统自带的触发器")
public class AverageValueTrigger {
    /**
     * 数字类型的告警触发器：
     *
     * @param objects 触发器传递进来的数据为一批对象，每一个对象都携带一批历史数据，至少有一个对象
     * @param params  触发器传递进来的数据处理条件
     * @return 生成同名的数值对象
     */
    @FoxEdgeTriggerMethod(name = "数字.平均.次数.均值触发器")
    public static Map<String, Object> numberAveTrigger(Map<String, List<ObjectValue>> objects, Map<String, Object> params) {
        Object size = params.get("size");

        // 检查：阈值是否为整数
        if (!isDigit(size)) {
            throw new ServiceException("size必须是整数");
        }

        long length = Long.parseLong(size.toString());
        if (length < 1 && length > 10) {
            throw new ServiceException("size必须在1～10之间");
        }

        // 比较判定
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, List<ObjectValue>> entry : objects.entrySet()) {
            String key = entry.getKey();
            List<ObjectValue> valueList = entry.getValue();

            // 检查：队列中是否有数据
            if (valueList.isEmpty()) {
                continue;
            }

            // 到达的数据量还没有达到要求
            if (valueList.size() < length) {
                continue;
            }


            Long sumLong = 0L;
            Double sumDouble = 0.0;

            double sum = 0.0;
            Object sample = null;
            for (int i = 0; i < length; i++) {
                // 取出尾部的数据
                ObjectValue objectValue = valueList.get(valueList.size() - 1);
                Object value = objectValue.getValue();

                sample = value;

                // <1>计算累计值
                if (value instanceof Long) {
                    sum += ((Long) value).longValue();
                    continue;
                }
                if (value instanceof Integer) {
                    sum += ((Integer) value).intValue();
                    continue;
                }
                if (value instanceof Short) {
                    sum += ((Short) value).shortValue();
                    continue;
                }
                if (value instanceof Byte) {
                    sum += ((Byte) value).byteValue();
                    continue;
                }
                if (value instanceof Double) {
                    sum += ((Double) value).doubleValue();
                    continue;
                }
                if (value instanceof Float) {
                    sum += ((Float) value).floatValue();
                    continue;
                }
            }

            // <2>计算平均值
            double ave = (sum) / length;

            // <3>转换数据类型
            if (sample instanceof Long) {
                sample = (long) ave;
            }
            if (sample instanceof Integer) {
                sample = (int) ave;
            }
            if (sample instanceof Short) {
                sample = (short) ave;
            }
            if (sample instanceof Byte) {
                sample = (byte) ave;
            }
            if (sample instanceof Double) {
                sample = ave;
            }
            if (sample instanceof Float) {
                sample = (float) ave;
            }

            // 保存结果
            result.put(key, sample);
        }

        return result;
    }

    /**
     * 是否为数字
     *
     * @param value
     * @return
     */
    private static boolean isNumber(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Short) {
            return true;
        }
        if (value instanceof Integer) {
            return true;
        }
        if (value instanceof Long) {
            return true;
        }
        if (value instanceof Double) {
            return true;
        }

        if (value instanceof Float) {
            return true;
        }

        return value instanceof Byte;
    }

    private static boolean isDigit(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Short) {
            return true;
        }
        if (value instanceof Integer) {
            return true;
        }
        return value instanceof Long;

    }
}
