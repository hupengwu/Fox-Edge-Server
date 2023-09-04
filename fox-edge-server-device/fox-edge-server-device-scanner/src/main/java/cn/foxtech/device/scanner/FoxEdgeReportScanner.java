package cn.foxtech.device.scanner;

import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperateParam;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeReport;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeReportMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FoxEdgeReportScanner {
    /**
     * 扫描代码，生成操作名-函数映射表,"cn.foxtech.device.adapter.annotation"
     *
     * @param pack 包名称
     * @return 函数映射表结构：device-operater-methodpair
     */
    public static Map<String, Map<String, FoxEdgeReportMethod>> scanMethodPair(String pack) {
        Map<String, Map<String, FoxEdgeReportMethod>> deviceType2operater = new HashMap<>();
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
                Map<String, FoxEdgeReportMethod> operater2methodpair = deviceType2operater.get(typeAnnotation.value());
                if (operater2methodpair == null) {
                    operater2methodpair = new HashMap<String, FoxEdgeReportMethod>();
                    deviceType2operater.put(typeAnnotation.value(), operater2methodpair);
                }

                // 函数级别的处理
                Method[] methods = aClass.getMethods();
                for (Method method : methods) {
                    // 是否为操作函数函数
                    if (!method.isAnnotationPresent(FoxEdgeOperate.class)) {
                        continue;
                    }

                    // 是否为事件函数
                    if (!method.isAnnotationPresent(FoxEdgeReport.class)) {
                        continue;
                    }

                    // 检查：是否为解码函数
                    FoxEdgeOperate operAnnotation = method.getAnnotation(FoxEdgeOperate.class);
                    if (!FoxEdgeOperate.decoder.equals(operAnnotation.type())) {
                        continue;
                    }

                    // 检查：上次是否保存了函数
                    FoxEdgeReportMethod methodPair = operater2methodpair.get(operAnnotation.name());
                    if (methodPair == null) {
                        methodPair = new FoxEdgeReportMethod();
                        operater2methodpair.put(operAnnotation.name(), methodPair);
                    }

                    FoxEdgeReport eventAnnotation = method.getAnnotation(FoxEdgeReport.class);

                    // 获取FoxEdgeMethodParam注解上的参数信息
                    Map<String, String> params = getFoxEdgeMethodParam(method.getAnnotation(FoxEdgeOperateParam.class));


                    // 记录注解输入的参数
                    methodPair.setManufacturer(typeAnnotation.manufacturer());
                    methodPair.setDeviceType(typeAnnotation.value());
                    methodPair.setName(operAnnotation.name());
                    methodPair.setMode(operAnnotation.mode());
                    methodPair.setType(eventAnnotation.type());
                    methodPair.setDecoderMethod(method);

                }
            }
        } catch (Throwable e) {
            e.getCause();
            e.printStackTrace();
        }

        Map<String, Map<String, FoxEdgeReportMethod>> result = new HashMap<>();
        for (String key : deviceType2operater.keySet()) {
            if (deviceType2operater.get(key).size() > 0) {
                result.put(key, deviceType2operater.get(key));
            }
        }

        return result;
    }

    /**
     * 默认扫描的范围是cn.foxtech.device
     *
     * @return
     */
    public static Map<String, Map<String, FoxEdgeReportMethod>> scanMethodPair() {
        return scanMethodPair(RootLocation.class.getPackage().getName());
    }


    public static void main(String[] args) {
        Map<String, Map<String, FoxEdgeReportMethod>> deviceType2operater = scanMethodPair("cn.foxtech.device.protocol");
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
