package cn.foxtech.common.entity.entity;

import cn.foxtech.common.utils.MapUtils;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_device_value_task")
public class DeviceValueExTaskEntity extends DeviceValueExTaskBase {
    /**
     * JSON参数
     */
    private Map<String, Object> taskParam = new HashMap<>();


    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        return super.makeServiceKeyList();
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        return super.makeWrapperKey();
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.taskParam);

        return list;
    }

    public void updateTaskParam(Map<String, Object> input) {
        Map<String, Object> param1 = this.taskParam;
        Map<String, Object> input1 = input;

        // 1级参数
        MapUtils.copyValue(param1, "cacheSize", input1, "cacheSize", 1);
        MapUtils.copyValue(param1, "methodScript", input1, "methodScript", "");
        MapUtils.copyValue(param1, "description", input1, "description", "");
        MapUtils.copyValue(param1, "dataSource", input1, "dataSource", new HashMap<>());

        // 2级参数
        Map<String, Object> param2 = (Map<String, Object>) param1.get("dataSource");
        Map<String, Object> input2 = (Map<String, Object>) input1.get("dataSource");
        MapUtils.copyValue(param2, "deviceType", input2, "deviceType", "");
        MapUtils.copyValue(param2, "manufacturer", input2, "manufacturer", "");
        MapUtils.copyValue(param2, "deviceType", input2, "deviceType", "");
        MapUtils.copyValue(param2, "deviceName", input2, "deviceName", "");
        MapUtils.copyValue(param2, "dataSource", input2, "dataSource", new HashMap<>());

        // 3级参数
        Map<String, Object> param3 = (Map<String, Object>) param2.get("dataObject");
        Map<String, Object> input3 = (Map<String, Object>) input2.get("dataObject");
        MapUtils.copyValue(param2, "dataObject", input2, "dataObject", "");
        Collection objectSet = (Collection) param3.computeIfAbsent("objectName", k -> new HashSet<>());
        Collection objectList = (Collection) input3.computeIfAbsent("objectName", k -> new HashSet<>());
        objectSet.addAll(objectList);
    }

    public void updateScript(String script) {
        this.taskParam.put("methodScript", script);
    }

}
