package cn.foxtech.trigger.logic.simple.trigger;

import cn.foxtech.trigger.logic.common.FoxEdgeTriggerMethod;
import cn.foxtech.trigger.logic.common.FoxEdgeTriggerModel;
import cn.foxtech.trigger.logic.common.ObjectValue;
import cn.foxtech.core.exception.ServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FoxEdgeTriggerModel(name = "简单触发器", manufacturer = "FoxTeam", description = "系统自带的触发器")
public class SimpleAlarmTrigger {
    /**
     * 数字类型的告警触发器：
     *
     * @param objects 触发器传递进来的数据为一批对象，每一个对象都携带一批历史数据，至少有一个对象
     * @param params  触发器传递进来的数据处理条件
     * @return 生成同名的数值对象
     */
    @FoxEdgeTriggerMethod(name = "数字.比较.阈值.告警触发器")
    public static Map<String, Object> numberThresholdTrigger(Map<String, List<ObjectValue>> objects, Map<String, Object> params) {
        String operator = (String) params.get("operator");
        Object threshold = params.get("threshold");

        // 检查：阈值是否为数字
        if (!isNumber(threshold)) {
            throw new ServiceException("threshold必须是数字");
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

            // 取出尾部的数据
            ObjectValue objectValue = valueList.get(valueList.size() - 1);
            Object value = objectValue.getValue();

            // 检查：是否为数字
            if (!isNumber(value)) {
                continue;
            }

            if (">".equals(operator)) {
                result.put(key, Double.parseDouble(value.toString()) > Double.parseDouble(threshold.toString()));
                continue;
            }
            if (">=".equals(operator)) {
                result.put(key, Double.parseDouble(value.toString()) >= Double.parseDouble(threshold.toString()));
                continue;
            }
            if ("<".equals(operator)) {
                result.put(key, Double.parseDouble(value.toString()) < Double.parseDouble(threshold.toString()));
                continue;
            }
            if ("<=".equals(operator)) {
                result.put(key, Double.parseDouble(value.toString()) <= Double.parseDouble(threshold.toString()));
                continue;
            }
            if ("=".equals(operator)) {
                result.put(key, Double.parseDouble(value.toString()) == Double.parseDouble(threshold.toString()));
                continue;
            }
            if ("!=".equals(operator)) {
                result.put(key, Double.parseDouble(value.toString()) != Double.parseDouble(threshold.toString()));
                continue;
            }
        }


        return result;
    }

    /**
     * 数字类型的告警触发器
     *
     * @return
     */
    @FoxEdgeTriggerMethod(name = "布尔.比较.阈值.告警触发器")
    public static Map<String, Object> boolThresholdTrigger(Map<String, List<ObjectValue>> objects, Map<String, Object> params) {
        String operator = (String) params.get("operator");
        Object threshold = params.get("threshold");

        // 检查：阈值是否为布尔值
        if (!(threshold instanceof Boolean)) {
            throw new ServiceException("threshold必须是bool类型");
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

            // 取出尾部的数据
            ObjectValue objectValue = valueList.get(valueList.size() - 1);
            Object value = objectValue.getValue();

            if ("=".equals(operator)) {
                result.put(key, value == threshold);
                continue;
            }
            if ("!=".equals(operator)) {
                result.put(key, value != threshold);
                continue;
            }
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
}
