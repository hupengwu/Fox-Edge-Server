package cn.foxtech.channel.common.linker;


import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class LinkerMethodScanner {
    private static final Logger logger = Logger.getLogger(LinkerMethodScanner.class);

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private LinkerMethodTemplate methodTemplate;

    public static LinkerMethodEntity scanMethod(Class<?> aClass) {
        LinkerMethodEntity linkerMethodEntity = new LinkerMethodEntity();
        try {
            // 是否为解码器类型
            if (!aClass.isAnnotationPresent(FoxEdgeDeviceType.class)) {
                return null;
            }

            FoxEdgeDeviceType typeAnnotation = aClass.getAnnotation(FoxEdgeDeviceType.class);
            String deviceType = typeAnnotation.value();
            String manufacturer = typeAnnotation.manufacturer();

            linkerMethodEntity.setDeviceType(deviceType);
            linkerMethodEntity.setManufacturer(manufacturer);

            // 函数级别的处理
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                linkerMethodEntity.setMethod(method);
            }

            if (linkerMethodEntity.isNull()) {
                return null;
            }

            return linkerMethodEntity;

        } catch (Throwable e) {
            logger.error(e);
        }

        return null;
    }

    public void loadJar() {
        try {
            Map<String, Object> encoderJars = this.channelProperties.getLinkEncoderJars();
            for (String key : encoderJars.keySet()) {
                Object value = encoderJars.get(key);
                if (value == null) {
                    continue;
                }

                String jarFile = value.toString();
                JarLoaderUtils.loadJar(jarFile);
            }

            // 然后通过扫描注解，生成操作定义表

        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * 需要测试的代码
     * @param pack 包名称
     * @return 返回值
     */
    public Map<String, LinkerMethodEntity> scanMethod(String pack) {
        Map<String, LinkerMethodEntity> result = new HashMap<>();

        try {
            Set<Class<?>> classSet = JarLoaderUtils.getClasses(pack);
            for (Class<?> aClass : classSet) {
                LinkerMethodEntity linkerMethodEntity = scanMethod(aClass);
                if (linkerMethodEntity == null) {
                    continue;
                }

                result.put(linkerMethodEntity.getDeviceType(), linkerMethodEntity);
            }
        } catch (Throwable e) {
            logger.error(e);
        }

        return result;
    }

    /**
     * 默认扫描的范围是cn.foxtech.device
     */
    public void scanMethod() {
        Map<String, LinkerMethodEntity> methods = this.scanMethod(RootLocation.class.getPackage().getName());

        this.methodTemplate.getMap().putAll(methods);
    }
}
