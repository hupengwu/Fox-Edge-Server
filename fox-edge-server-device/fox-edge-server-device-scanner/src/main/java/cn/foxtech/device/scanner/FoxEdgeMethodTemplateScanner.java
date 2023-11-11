package cn.foxtech.device.scanner;

import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeExchangeMethod;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeMethodTemplate;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgePublishMethod;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeReportMethod;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class FoxEdgeMethodTemplateScanner {
    private static final Logger logger = Logger.getLogger(FoxEdgeMethodTemplateScanner.class);

    public static void scanMethodPair(List<String> jarFileNameList) {
        try {
            // 动态装载配置文件中指明的解码器JAR包
            for (String line : jarFileNameList) {
                line = line.trim();
                JarLoaderUtils.loadJar(line);
            }

            Map<String, Object> exchangeMethod = FoxEdgeExchangeScanner.scanMethodPair();
            Map<String, Object> reportMethod = FoxEdgeReportScanner.scanMethodPair();
            Map<String, Object> publishMethod = FoxEdgePublishScanner.scanMethodPair();

            // 然后通过扫描注解，生成操作定义表
            FoxEdgeMethodTemplate.inst().setExchangeMethod(exchangeMethod);
            FoxEdgeMethodTemplate.inst().setReportMethod(reportMethod);
            FoxEdgeMethodTemplate.inst().setPublishMethod(publishMethod);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
