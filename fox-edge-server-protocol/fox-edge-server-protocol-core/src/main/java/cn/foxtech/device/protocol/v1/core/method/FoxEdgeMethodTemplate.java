package cn.foxtech.device.protocol.v1.core.method;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FoxEdgeMethodTemplate {
    private static final FoxEdgeMethodTemplate template = new FoxEdgeMethodTemplate();

    private Map<String, Object> exchangeMethod = new HashMap<>();

    private Map<String, Object> reportMethod = new HashMap<>();

    private Map<String, Object> publishMethod = new HashMap<>();

    public static FoxEdgeMethodTemplate inst() {
        return template;
    }

    public Map<String, FoxEdgeExchangeMethod> getExchangeMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.exchangeMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, FoxEdgeExchangeMethod>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

    public Map<String, FoxEdgePublishMethod> getPublishMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.publishMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, FoxEdgePublishMethod>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

    public Map<String, FoxEdgeReportMethod> getReportMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.reportMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, FoxEdgeReportMethod>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

}
