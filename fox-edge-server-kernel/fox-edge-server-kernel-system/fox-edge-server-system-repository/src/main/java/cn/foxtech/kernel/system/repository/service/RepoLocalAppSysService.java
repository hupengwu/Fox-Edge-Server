package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.process.ProcessUtils;
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
    private RedisConsoleService redisConsoleService;

    @Autowired
    private SysProcessConfigService processConfigService;

    public List<Map<String, Object>> getSysProcessList() {
        Map<String, Object> configValue = this.processConfigService.getConfigValue();
        String mysqlServer = (String) configValue.get("mysql-server");
        String redisServer = (String) configValue.get("redis-server");

        List<Map<String, Object>> mapList = new ArrayList<>();

        // 获得进程信息：mysqlServer
        try {
            Map<String, Object> map = ProcessUtils.getSysProcess(mysqlServer);
            map.put(ServiceVOFieldConstant.field_app_type, ServiceVOFieldConstant.field_type_kernel);
            map.put(ServiceVOFieldConstant.field_app_name, "mysql-server");

            Long pid = Long.parseLong(map.get("pid").toString());
            Set<Long> ports = ProcessUtils.getProcessPort(pid);
            String port = ports.toString().replace("[", "");
            port = port.replace("]", "");
            map.put(ServiceVOFieldConstant.field_app_port, port);

            mapList.add(map);
        } catch (Exception e) {
            this.redisConsoleService.error("获得mysql-server进程信息失败:" + e.getMessage());
        }
        // 获得进程信息：redisServer
        try {
            Map<String, Object> map = ProcessUtils.getSysProcess(redisServer);
            map.put(ServiceVOFieldConstant.field_app_type, ServiceVOFieldConstant.field_type_kernel);
            map.put(ServiceVOFieldConstant.field_app_name, "redis-server");

            Long pid = Long.parseLong(map.get("pid").toString());
            Set<Long> ports = ProcessUtils.getProcessPort(pid);
            String port = ports.toString().replace("[", "");
            port = port.replace("]", "");
            map.put(ServiceVOFieldConstant.field_app_port, port);

            mapList.add(map);
        } catch (Exception e) {
            this.redisConsoleService.error("获得redis-server进程信息失败:" + e.getMessage());
        }


        return mapList;

    }
}
