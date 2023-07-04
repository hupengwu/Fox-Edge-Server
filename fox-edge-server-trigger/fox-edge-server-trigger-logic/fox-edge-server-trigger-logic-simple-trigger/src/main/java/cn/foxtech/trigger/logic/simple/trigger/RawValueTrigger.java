package cn.foxtech.trigger.logic.simple.trigger;

import cn.foxtech.trigger.logic.common.FoxEdgeTriggerMethod;
import cn.foxtech.trigger.logic.common.FoxEdgeTriggerModel;
import cn.foxtech.trigger.logic.common.ObjectValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FoxEdgeTriggerModel(name = "原生值触发器", manufacturer = "FoxTeam", description = "系统自带的触发器")
public class RawValueTrigger {
    /**
     * 原生数值触发器，它的作用仅仅是为了方便在生成一个跟原生数据相同的触发器值实体
     *
     * @param objects
     * @param params
     * @return
     */
    @FoxEdgeTriggerMethod(name = "对象.相同.原生触发器")
    public static Map<String, Object> rawValueTrigger(Map<String, List<ObjectValue>> objects, Map<String, Object> params) {
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
            result.put(key, value);
        }

        return result;
    }
}
