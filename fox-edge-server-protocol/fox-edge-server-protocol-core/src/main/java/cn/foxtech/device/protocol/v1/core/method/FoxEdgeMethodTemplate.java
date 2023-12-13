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

    public Map<String, Object> getExchangeMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.exchangeMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, Object>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

    public Map<String, Object> getPublishMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.publishMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, Object>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

    public Map<String, Object> getReportMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.reportMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, Object>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

}
