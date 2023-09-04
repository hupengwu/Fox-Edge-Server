package cn.foxtech.device.protocol.v1.core.method;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FoxEdgeMethodTemplate {
    private static final FoxEdgeMethodTemplate template = new FoxEdgeMethodTemplate();

    private Map<String, Map<String, FoxEdgeExchangeMethod>> exchangeMethod = new HashMap<>();

    private Map<String, Map<String, FoxEdgeReportMethod>> reportMethod = new HashMap<>();

    private Map<String, Map<String, FoxEdgePublishMethod>> publishMethod = new HashMap<>();

    public static FoxEdgeMethodTemplate inst() {
        return template;
    }

}
