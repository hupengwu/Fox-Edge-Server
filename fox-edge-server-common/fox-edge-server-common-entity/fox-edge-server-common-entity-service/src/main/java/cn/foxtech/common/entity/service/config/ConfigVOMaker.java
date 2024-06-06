package cn.foxtech.common.entity.service.config;

import cn.foxtech.common.entity.constant.ConfigParamVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigVOMaker {
    @Setter(value = AccessLevel.PUBLIC)
    private ISaveMode saveMode;

    public static ConfigVOMaker build() {
        return new ConfigVOMaker();
    }

    public void postProcess(ConfigEntity entity) {
        this.process(entity, null);
    }

    public void preProcess(ConfigEntity entity, ConfigEntity exist) {
        this.process(entity, exist);
    }

    private void process(ConfigEntity entity, ConfigEntity exist) {
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
            Map<String, Object> value = null;
            if (exist != null) {
                value = exist.getConfigValue();
            }

            process(param, configValue, value);
        }
    }

    /**
     * 后期加工
     *
     * @param entityList  实体列表
     * @param processMode 加工模式
     * @return 实体列表
     */
    public List<BaseEntity> postProcess(List<BaseEntity> entityList, String processMode) {
        List<BaseEntity> result = new ArrayList<>();

        for (BaseEntity base : entityList) {
            // clone一个副本，避免接下来的修改操作影响到源数据
            ConfigEntity clone = ((ConfigEntity) base).clone();
            result.add(clone);

            postProcess(clone);
        }

        return result;
    }

    /**
     * 后期处理
     *
     * @param entityList 加工前的实体列表
     * @return 加工后的实体列表
     */
    public List<BaseEntity> postProcess(List<BaseEntity> entityList) {
        List<BaseEntity> result = new ArrayList<>();

        for (BaseEntity base : entityList) {
            // 利用JSON转换的过程，深度clone一个副本，避免接下来的修改操作影响到源数据
            ConfigEntity clone = (ConfigEntity) JsonUtils.clone(base);
            postProcess(clone);

            result.add(clone);
        }

        return result;
    }


    /**
     * 数据处理
     * 如果填写了existValue，那么为预处理，否则为后期处理
     *
     * @param param       参数
     * @param configValue 输入的配置数值
     * @param existValue  当前的配置数据
     */
    private void process(Map<String, Object> param, Map<String, Object> configValue, Map<String, Object> existValue) {
        try {
            String fieldName = (String) param.get("fieldName");
            String valueType = (String) param.get("valueType");
            Object defaultValue = param.get("defaultValue");
            String showMode = (String) param.get("showMode");
            String saveMode = (String) param.get("saveMode");

            Object[] fields = fieldName.split("\\.");

            // 取出输入的新数值
            Object newValue = MapUtils.getValue(configValue, fields);
            if (newValue == null) {
                newValue = defaultValue;
            }

            // 取出已经存在的旧数值
            Object oldValue = null;
            if (existValue != null) {
                oldValue = MapUtils.getValue(existValue, fields);
                if (oldValue == null) {
                    oldValue = defaultValue;
                }
            }

            // 至少包含fieldName和valueType字段
            if (MethodUtils.hasEmpty(newValue)) {
                return;
            }

            // 验证数值的类型是否正确
            if (!verifyValueType(valueType, newValue)) {
                return;
            }

            // 下面是各种具体行为


            // 隐藏信息用的覆盖字符串
            String hideString = "****";

            if (existValue == null) {
                // 后期处理

                // 场景1：密码的安全显示
                if ("Security".equals(showMode)) {
                    MapUtils.setValue(configValue, fields, hideString);
                }
            } else {
                // 前期处理

                // 场景1：密码的原文存储：如果新的数据是"****"，说明用户并没有修改
                if ("Security".equals(showMode) && hideString.equals(newValue)) {
                    String realValue = "";
                    if (oldValue != null) {
                        realValue = oldValue.toString();
                    }
                    // 将真实的数据，更新到用户的输入数据之中
                    MapUtils.setValue(configValue, fields, realValue);
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
