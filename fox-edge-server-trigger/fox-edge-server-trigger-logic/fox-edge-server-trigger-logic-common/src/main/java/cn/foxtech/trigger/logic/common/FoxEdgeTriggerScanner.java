package cn.foxtech.trigger.logic.common;


import cn.foxtech.common.utils.reflect.JarLoaderUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 触发器扫描器
 */
public class FoxEdgeTriggerScanner {
    /**
     * 扫描代码，触发器函数表
     *
     * @param pack 包名称
     * @return 函数映射表结构：modelName-methodName-trigger
     */
    public static Map<String, Map<String, FoxEdgeTrigger>> scanTrigger(String pack) {
        Map<String, Map<String, FoxEdgeTrigger>> modelName2methodName2trigger = new HashMap<>();
        try {
            Set<Class<?>> classSet = JarLoaderUtils.getClasses(pack);
            for (Class<?> aClass : classSet) {
                // 是否为触发器类型
                if (!aClass.isAnnotationPresent(FoxEdgeTriggerModel.class)) {
                    continue;
                }

                // 设备级别的处理：
                FoxEdgeTriggerModel modelAnnotation = aClass.getAnnotation(FoxEdgeTriggerModel.class);
                String modelName = modelAnnotation.name();


                Map<String, FoxEdgeTrigger> methodName2trigger = modelName2methodName2trigger.get(modelName);
                if (methodName2trigger == null) {
                    methodName2trigger = new HashMap<String, FoxEdgeTrigger>();
                    modelName2methodName2trigger.put(modelName, methodName2trigger);
                }

                // 函数级别的处理
                Method[] methods = aClass.getMethods();
                for (Method method : methods) {
                    // 是否为触发器函数
                    if (!method.isAnnotationPresent(FoxEdgeTriggerMethod.class)) {
                        continue;
                    }

                    FoxEdgeTriggerMethod methodAnnotation = method.getAnnotation(FoxEdgeTriggerMethod.class);
                    String methodName = methodAnnotation.name();

                    FoxEdgeTrigger trigger = methodName2trigger.get(methodName);
                    if (trigger == null) {
                        trigger = new FoxEdgeTrigger();
                        methodName2trigger.put(methodAnnotation.name(), trigger);
                    }


                    trigger.setModelName(modelName);
                    trigger.setMethodName(methodName);
                    trigger.setMethod(method);
                    trigger.setManufacturer(modelAnnotation.manufacturer());
                }
            }
        } catch (Throwable e) {
            e.getCause();
            e.printStackTrace();
        }

        return modelName2methodName2trigger;
    }

    /**
     * 默认扫描的范围是cn.foxtech.trigger.logic
     *
     * @return modelName-methodName-FoxEdgeTrigger
     */
    public static Map<String, Map<String, FoxEdgeTrigger>> scanTrigger() {
        return scanTrigger("cn.foxtech.trigger.logic");
    }
}
