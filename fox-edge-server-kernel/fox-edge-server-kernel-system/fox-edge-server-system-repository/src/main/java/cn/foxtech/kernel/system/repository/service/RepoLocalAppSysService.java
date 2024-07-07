package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.kernel.common.constants.EdgeServiceConstant;
import cn.foxtech.kernel.common.service.EdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 获得mysql-server和redis-server的进程信息
 */
@Component
public class RepoLocalAppSysService {
    @Autowired
    private InitialConfigService configService;

    @Autowired
    private EdgeService edgeService;


    public List<Map<String, Object>> getSysProcessList() {
        Map<String, Object> valueConfig = this.configService.getConfigParam("systemProcessConfig");
        String mysqlServer = (String) valueConfig.get("mysql-server");
        String mariaServer = (String) valueConfig.get("maria-server");
        String redisServer = (String) valueConfig.get("redis-server");
        String nacosServer = (String) valueConfig.get("nacos-server");
        String nodeRed = (String) valueConfig.get("node-red");

        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> appMap = null;

        // 获得进程信息：mysqlServer
        if (!MethodUtils.hasEmpty(mysqlServer)) {
            appMap = this.getAppInfo(ServiceVOFieldConstant.field_type_kernel, "mysql-server", mysqlServer, true);
            if (!MethodUtils.hasEmpty(appMap)) {
                mapList.add(appMap);
            }
        }


        // 获得进程信息：mysqlServer
        if (!MethodUtils.hasEmpty(mariaServer)) {
            appMap = this.getAppInfo(ServiceVOFieldConstant.field_type_kernel, "maria-server", mariaServer, true);
            if (!MethodUtils.hasEmpty(appMap)) {
                mapList.add(appMap);
            }
        }


        // 获得进程信息：redisServer
        if (!MethodUtils.hasEmpty(redisServer)) {
            appMap = this.getAppInfo(ServiceVOFieldConstant.field_type_kernel, "redis-server", redisServer, true);
            if (!MethodUtils.hasEmpty(appMap)) {
                mapList.add(appMap);
            }
        }

        // 获得进程信息：nacosServer
        if (!MethodUtils.hasEmpty(nacosServer) && this.edgeService.getWorkMode().equals(EdgeServiceConstant.value_work_mode_cloud)) {
            appMap = this.getAppInfo(ServiceVOFieldConstant.field_type_kernel, "nacos-server", nacosServer, false);
            if (!MethodUtils.hasEmpty(appMap)) {
                mapList.add(appMap);
            }
        }


        // 获得进程信息：node-red
        if (!MethodUtils.hasEmpty(nodeRed)) {
            appMap = this.getAppInfo(ServiceVOFieldConstant.field_type_kernel, "node-red", nodeRed, true);
            if (!MethodUtils.hasEmpty(appMap)) {
                mapList.add(appMap);
            }
        }


        return mapList;

    }

    private Map<String, Object> getAppInfo(String appType, String appName, String appFeature, boolean isSysParam) {
        try {
            Map<String, Object> map = ProcessUtils.getSysProcess(appFeature, isSysParam);
            map.put(ServiceVOFieldConstant.field_app_type, appType);
            map.put(ServiceVOFieldConstant.field_app_name, appName);
            map.put(ServiceVOFieldConstant.field_app_load, true);

            Long pid = Long.parseLong(map.get("pid").toString());
            Set<Long> ports = ProcessUtils.getProcessPort(pid);
            String port = ports.toString().replace("[", "");
            port = port.replace("]", "");
            map.put(ServiceVOFieldConstant.field_app_port, port);
            return map;
        } catch (Exception e) {
            // 这是循环调用的方法，所以不打印信息
            e.getMessage();
        }

        return null;
    }
}
