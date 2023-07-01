package cn.foxtech.device.protocol.core.template;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateFactory {
    private static final Map<String, TemplateContainer> map = new ConcurrentHashMap<>();

    /**
     * 协议模块名称
     *
     * @param protocolModelName 协议模型明名称
     * @return 模板容器
     */
    public static TemplateContainer getTemplate(String protocolModelName) {
        TemplateContainer template = map.get(protocolModelName);
        if (template == null) {
            template = new TemplateContainer();
            map.put(protocolModelName, template);
        }

        return template;
    }
}
