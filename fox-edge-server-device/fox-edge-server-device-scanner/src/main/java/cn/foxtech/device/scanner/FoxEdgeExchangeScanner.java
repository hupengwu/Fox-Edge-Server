package cn.foxtech.device.scanner;


import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperateParam;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.device.protocol.core.method.FoxEdgeExchangeMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FoxEdgeExchangeScanner {
    /**
     * 扫描代码，生成操作名-函数映射表,"cn.foxtech.device.adapter.annotation"
     *
     * @param pack 包名称
     * @return 函数映射表结构：device-operater-methodpair
     */
    public static Map<String, Map<String, FoxEdgeExchangeMethod>> scanMethodPair(String pack) {
        Map<String, Map<String, FoxEdgeExchangeMethod>> deviceType2operater = new HashMap<>();
        try {
            Set<Class<?>> classSet = JarLoaderUtils.getClasses(pack);
            for (Class<?> aClass : classSet) {
                String name = aClass.getName();

                // 是否为解码器类型
                if (!aClass.isAnnotationPresent(FoxEdgeDeviceType.class)) {
                    continue;
                }

                // 设备级别的处理：
                FoxEdgeDeviceType typeAnnotation = aClass.getAnnotation(FoxEdgeDeviceType.class);
                String deviceType = typeAnnotation.value();
                String manufacturer = typeAnnotation.manufacturer();


                Map<String, FoxEdgeExchangeMethod> operater2methodpair = deviceType2operater.get(deviceType);
                if (operater2methodpair == null) {
                    operater2methodpair = new HashMap<String, FoxEdgeExchangeMethod>();
                    deviceType2operater.put(deviceType, operater2methodpair);
                }

                Map<String, FoxEdgeExchangeMethod> methodpairs = scanMethodPair(manufacturer, deviceType, aClass);
                operater2methodpair.putAll(methodpairs);
            }
        } catch (Throwable e) {
            e.getCause();
            e.printStackTrace();
        }

        Map<String, Map<String, FoxEdgeExchangeMethod>> result = new HashMap<>();
        for (String key : deviceType2operater.keySet()) {
            if (deviceType2operater.get(key).size() > 0) {
                result.put(key, deviceType2operater.get(key));
            }
        }

        return deviceType2operater;
    }

    public static Map<String, FoxEdgeExchangeMethod> scanMethodPair(String manufacturer, String deviceType, Class<?> aClass) {
        Map<String, FoxEdgeExchangeMethod> operater2methodpair = new HashMap<>();
        try {
            // 是否为解码器类型
            if (!aClass.isAnnotationPresent(FoxEdgeDeviceType.class)) {
                return operater2methodpair;
            }

            // 函数级别的处理
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                // 是否为解码函数
                if (!method.isAnnotationPresent(FoxEdgeOperate.class)) {
                    continue;
                }

                // 检查：上次是否保存了函数
                FoxEdgeOperate operAnnotation = method.getAnnotation(FoxEdgeOperate.class);
                FoxEdgeExchangeMethod methodPair = operater2methodpair.get(operAnnotation.name());
                if (methodPair == null) {
                    methodPair = new FoxEdgeExchangeMethod();
                    operater2methodpair.put(operAnnotation.name(), methodPair);
                }

                // 获取FoxEdgeMethodParam注解上的参数信息
                Map<String, String> params = getFoxEdgeMethodParam(method.getAnnotation(FoxEdgeOperateParam.class));


                // 记录注解输入的参数
                methodPair.setManufacturer(manufacturer);
                methodPair.setDeviceType(deviceType);
                methodPair.setName(operAnnotation.name());
                methodPair.setPolling(operAnnotation.polling());

                // 判定编码/解码类型
                if (FoxEdgeOperate.encoder.equals(operAnnotation.type())) {
                    methodPair.setEncoderMethod(method);
                    methodPair.setEncoderParams(params);
                }
                if (FoxEdgeOperate.decoder.equals(operAnnotation.type())) {
                    methodPair.setTimeout(operAnnotation.timeout());
                    methodPair.setDecoderMethod(method);
                    methodPair.setDecoderParams(params);

                    // 模式：状态模式/记录模式
                    methodPair.setMode(operAnnotation.mode());
                }


            }
        } catch (Throwable e) {
            e.getCause();
            e.printStackTrace();
        }

        // 筛选出同时又编码和解码函数的数据
        Map<String, FoxEdgeExchangeMethod> result = new HashMap<>();
        for (Map.Entry<String, FoxEdgeExchangeMethod> entry : operater2methodpair.entrySet()) {
            FoxEdgeExchangeMethod operateMethod = entry.getValue();

            if (operateMethod.getDecoderMethod() != null && operateMethod.getEncoderMethod() != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }


        return result;
    }

    /**
     * 默认扫描的范围是cn.foxtech.device
     *
     * @return
     */
    public static Map<String, Map<String, FoxEdgeExchangeMethod>> scanMethodPair() {
        return scanMethodPair(RootLocation.class.getPackage().getName());
    }

    /**
     * 获取FoxEdgeMethodParam注解上的信息
     *
     * @param paramAnnotation
     * @return
     */
    private static Map<String, String> getFoxEdgeMethodParam(FoxEdgeOperateParam paramAnnotation) {
        Map<String, String> params = new HashMap<>();
        if (paramAnnotation == null) {
            return params;
        }

        for (int i = 0; i < paramAnnotation.names().length; i++) {
            String paramName = paramAnnotation.names()[i];
            String paramValue = "";
            if (i < paramAnnotation.values().length) {
                paramValue = paramAnnotation.values()[i];
            }

            params.put(paramName, paramValue);
        }

        return params;
    }
}