package cn.foxtech.huawei.iotda.service.entity.property.gateway;

import cn.foxtech.huawei.iotda.service.entity.property.Service;
import cn.foxtech.huawei.iotda.service.entity.utils.TimeUtils;

import java.util.Map;

public class GatewayPropertyReportBuilder {
    /**
     * 网关自己的属性上报
     * @param dataMap 分类-》一堆属性
     * @return 网关的属性上报
     */
    public GatewayPropertyReport gateway_property_report_request(Map<String, Map<String, Object>> dataMap) {
        GatewayPropertyReport report = new GatewayPropertyReport();
        for (String key : dataMap.keySet()) {
            Map<String, Object> map = dataMap.get(key);

            Service service = new Service();
            service.setService_id(key);
            service.setProperties(map);
            service.setEvent_time(TimeUtils.getUTCTime());

            report.getServices().add(service);
        }

        return report;
    }
}
