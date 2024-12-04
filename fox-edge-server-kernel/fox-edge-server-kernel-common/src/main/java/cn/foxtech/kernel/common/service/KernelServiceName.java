package cn.foxtech.kernel.common.service;

import org.springframework.stereotype.Component;

@Component
public class KernelServiceName {
    public boolean isGateWay(String appType, String appName) {
        return "kernel".equals(appType) && ("gateway-service".equals(appName) || "gateway-native".equals(appName));
    }

    public boolean isManagerNative(String appType, String appName) {
        return "kernel".equals(appType) && "manager-native".equals(appName);
    }

    public String getAppName(String appType, String appName) {
        if ("kernel".equals(appType) && (appName.equals("gateway-service") || appName.equals("gateway-native"))) {
            return this.getGatewayName();
        }
        if ("kernel".equals(appType) && (appName.equals("manager-service") || appName.equals("manager-native"))) {
            return this.getManagerName();
        }

        return appName;
    }

    public String getManagerName() {
        return "manager-service";
    }

    public String getGatewayName() {
        return "gateway-service";
    }
}
