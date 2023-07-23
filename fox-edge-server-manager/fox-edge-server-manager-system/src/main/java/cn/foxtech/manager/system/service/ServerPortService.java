package cn.foxtech.manager.system.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.shell.ShellUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.utils.ServiceIniFilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 分配端口服务
 */
@Component
public class ServerPortService {
    @Autowired
    private ManageConfigService configService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    /**
     * 获得当前服务的业务端口
     *
     * @param applicationName
     * @param applicationType
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private Integer getServicePort(String applicationName, String applicationType, Long appPid) throws IOException, InterruptedException {
        Map<String, Object> serviceStartConfig = this.configService.getConfigValue(this.foxServiceName, this.foxServiceType, "serviceStartConfig");
        List<Map<String, Object>> services = (List<Map<String, Object>>) serviceStartConfig.getOrDefault("services", new ArrayList<>());

        Map<String, Object> appMap = null;
        for (Map<String, Object> service : services) {
            // 找到匹配的项目
            String appName = (String) service.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) service.get(ServiceVOFieldConstant.field_app_type);
            if (!applicationName.equals(appName)) {
                continue;
            }
            if (!applicationType.equals(appType)) {
                continue;
            }

            appMap = service;
            break;
        }

        // 检查：配置是否存在
        if (appMap != null) {
            // 检查：端口是否存在，不存在则分配端口
            if (appMap.containsKey(ServiceVOFieldConstant.field_app_port)) {
                // 检查：端口是否已经被其他程序占用
                Integer appPort = (Integer) appMap.get(ServiceVOFieldConstant.field_app_port);
                List<String> shellLineList = ShellUtils.executeShell("netstat -anp | grep " + appPort);
                if (shellLineList.isEmpty()) {
                    return appPort;
                }

                // 检查：使用该端口的进程ID是否存在，并且被自己使用，那么就直接沿用该端口
                Long pid = ProcessUtils.findPidByPort(appPort);
                if (pid != null && pid.equals(appPid)) {
                    return appPort;
                }

                // 如果已经被占用，则重新分配一个端口
                appPort = this.newServicePort();
                appMap.put(ServiceVOFieldConstant.field_app_port, this.newServicePort());
                this.configService.saveConfigValue(this.foxServiceName, this.foxServiceType, "serviceStartConfig", serviceStartConfig);
                return appPort;
            } else {
                Integer appPort = this.newServicePort();
                appMap.put(ServiceVOFieldConstant.field_app_port, this.newServicePort());
                this.configService.saveConfigValue(this.foxServiceName, this.foxServiceType, "serviceStartConfig", serviceStartConfig);
                return appPort;
            }
        } else {
            Integer appPort = this.newServicePort();

            appMap = new HashMap<>();
            appMap.put(ServiceVOFieldConstant.field_app_name, applicationName);
            appMap.put(ServiceVOFieldConstant.field_app_type, applicationType);
            appMap.put(ServiceVOFieldConstant.field_app_port, appPort);
            services.add(appMap);

            this.configService.saveConfigValue(this.foxServiceName, this.foxServiceType, "serviceStartConfig", serviceStartConfig);
            return appPort;
        }
    }

    /**
     * 分配新业务的端口号
     *
     * @throws IOException          异常信息
     * @throws InterruptedException 异常信息
     */
    private Integer newServicePort() throws IOException, InterruptedException {
        Map<String, Object> serviceStartConfig = this.configService.getConfigValue(this.foxServiceName, this.foxServiceType, "serviceStartConfig");
        List<Map<String, Object>> services = (List<Map<String, Object>>) serviceStartConfig.getOrDefault("services", new ArrayList<>());

        // 统计：已经分配给服务的端口
        Set<Integer> allocPorts = new HashSet<>();
        for (Map<String, Object> service : services) {
            Integer appPort = (Integer) service.get(ServiceVOFieldConstant.field_app_port);
            if (appPort == null) {
                continue;
            }

            allocPorts.add(appPort);
        }

        // 找出：尚未分配的端口
        Integer appPort = 9000;
        while (true) {
            appPort++;

            // 检查：这个端口是否已经在Linux操作系统中，被分配给其他进程使用
            List<String> result = ShellUtils.executeShell("netstat -anp | grep " + appPort);
            if (!result.isEmpty()) {
                continue;
            }

            // 检查：这个端口是否已经分配给其他服务（这些服务可能尚未启动，尚未占用操作系统的端口）           ;
            if (allocPorts.contains(appPort)) {
                continue;
            }

            return appPort;
        }
    }

    public Integer getServicePort(String appName, String appType) throws IOException, InterruptedException {
        // 简单校验参数
        if (MethodUtils.hasEmpty(appName, appType)) {
            throw new ServiceException("参数不能为空:appName, appType");
        }

        List<Map<String, Object>> iniFileInfoList = ServiceIniFilesUtils.getConfFileInfoList();
        ProcessUtils.extendAppStatus(iniFileInfoList);

        Long appPid = null;
        Integer appPort = null;
        for (Map<String, Object> map : iniFileInfoList) {
            if (!appName.equals(map.get(ServiceVOFieldConstant.field_app_name))) {
                continue;
            }
            if (!appType.equals(map.get(ServiceVOFieldConstant.field_app_type))) {
                continue;
            }

            appPid = NumberUtils.makeLong(map.getOrDefault(ServiceVOFieldConstant.field_pid, null));
            appPort = (Integer) (map.getOrDefault(ServiceVOFieldConstant.field_app_port, null));
            break;
        }


        // 检查：如果命令行参数和进程ID都存在，那么检查这个是否是该进程占用该端口
        if (appPort != null && appPid != null) {
            // 检查：使用该端口的进程ID是否存在，并且被自己使用，那么就直接沿用该端口，如果不是，那么重新分配端口
            Long pid = ProcessUtils.findPidByPort(appPort);
            if (pid == null || !pid.equals(appPid)) {
                appPort = this.getServicePort(appName, appType, appPid);
            }
        } else {
            // 重新分配端口
            appPort = this.getServicePort(appName, appType, appPid);
        }


        return appPort;
    }
}
