package cn.foxtech.common.entity.service.config;

import cn.foxtech.common.entity.constant.ConfigParamVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigVOMaker {
    public static final String mode_prev = "prev";
    public static final String mode_post = "post";

    @Setter(value = AccessLevel.PUBLIC)
    private ISaveMode saveMode;

    public static ConfigVOMaker build() {
        return new ConfigVOMaker();
    }

    public void process(ConfigEntity entity, String processMode) {
        Map<String, Object> configParam = entity.getConfigParam();
        Map<String, Object> configValue = entity.getConfigValue();
        if (MethodUtils.hasEmpty(configParam, configValue)) {
            return;
        }

        // 取出配置項
        List<Map<String, Object>> paramList = (List<Map<String, Object>>) configParam.get(ConfigParamVOFieldConstant.field_list);
        if (paramList == null || paramList.isEmpty()) {
            return;
        }

        for (Map<String, Object> param : paramList) {
            process(param, configValue, processMode);
        }

        return;
    }

    /**
     * 后期加工
     *
     * @param entityList 实体列表
     * @param processMode 加工模式
     * @return 实体列表
     */
    public List<BaseEntity> process(List<BaseEntity> entityList, String processMode) {
        List<BaseEntity> result = new ArrayList<>();

        for (BaseEntity base : entityList) {
            // clone一个副本，避免接下来的修改操作影响到源数据
            ConfigEntity clone = ((ConfigEntity) base).clone();
            result.add(clone);

            process(clone, processMode);
        }

        return result;
    }


    /**
     * 前期处理
     *
     * @param param 参数
     * @param configValue 配置值
     */
    private void process(Map<String, Object> param, Map<String, Object> configValue, String processMode) {
        try {
            String fieldName = (String) param.get("fieldName");
            String valueType = (String) param.get("valueType");
            Object defaultValue = param.get("defaultValue");
            String showMode = (String) param.get("showMode");
            String saveMode = (String) param.get("saveMode");

            // 至少包含fieldName和valueType字段
            if (MethodUtils.hasEmpty(fieldName, valueType)) {
                return;
            }

            // 验证数值的类型是否正确
            Object value = configValue.getOrDefault(fieldName, defaultValue);
            if (!verifyValueType(valueType, value)) {
                return;
            }

            // 下面是各种具体行为


            // 后期处理
            if (ConfigVOMaker.mode_post.equals(processMode)) {
                // 场景1：密码的安全显示
                if ("Security".equals(showMode)) {
                    configValue.put(fieldName, "****");
                }
            }
            // 前期处理
            if (ConfigVOMaker.mode_prev.equals(processMode)) {
                // 场景1：密码的加密存储
                if ("Security".equals(saveMode)) {
                    String newValue = this.saveMode.process(value);
                    configValue.put(fieldName, newValue);
                }
            }


        } catch (Exception e) {
            return;
        }

    }

    private boolean verifyValueType(String valueType, Object value) {
        if (valueType.equals("string")) {
            return value instanceof String;
        }

        return false;
    }

    public interface ISaveMode {
        /**
         * 找到目标对象，并直接元素的内容
         *
         * @param value 元素
         * @return 是否修改成功
         */
        String process(Object value);
    }
}
