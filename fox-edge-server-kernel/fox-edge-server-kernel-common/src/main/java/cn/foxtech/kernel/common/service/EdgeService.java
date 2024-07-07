package cn.foxtech.kernel.common.service;


import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.common.constants.EdgeServiceConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 边缘节点的信息：实际上只有CPUID信息
 */
@Component
public class EdgeService {
    private final Map<String, Object> map = new HashMap<>();
    /**
     * 命令行启动参数
     */
    @Autowired
    private ApplicationArguments arguments;

    /**
     * yml配置参数
     */
    @Autowired
    private AbstractEnvironment environment;

    /**
     * 获取主机信息：目前测试只有CPU的ID是可以成功获取的
     * CPU的ID能够获取，是因为CPU就英特尔和AMD在生产
     * 至于主板信息，工控机的生产厂家才懒的写
     * 至于网卡，各个计算机的网卡千差万别
     *
     * @return
     */
    public Map<String, Object> getOSInfo() {
        if (!this.map.containsKey("cpuId")) {
            this.map.put("cpuId", OSInfoUtils.getCPUID());
        }
        if (!this.map.containsKey(EdgeServiceConstant.filed_env_type)) {
            String envType = this.getAppArg("--env_type=", EdgeServiceConstant.value_env_type_device);
            this.map.put(EdgeServiceConstant.filed_env_type, envType);
        }


        return this.map;
    }

    private String getAppArg(String tag, String defaultValue) {
        String[] args = this.arguments.getSourceArgs();
        for (String arg : args) {
            if (!arg.startsWith(tag)) {
                continue;
            }

            return arg.substring(tag.length());
        }

        return defaultValue;
    }

    /**
     * 获取工作模式
     *
     * @return 工作模式
     */
    public String getWorkMode() {
        // 根据是否配置了nacos来判定，是否为本地模式还是云模式
        Object value = this.environment.getProperty("spring.cloud.nacos.discovery.server-addr", Object.class);
        if (value == null) {
            return EdgeServiceConstant.value_work_mode_local;
        } else {
            return EdgeServiceConstant.value_work_mode_cloud;
        }
    }

    /**
     * CPU的ID
     *
     * @return
     */
    public String getCPUID() {
        return this.getOSInfo().get("cpuId").toString();
    }

    /**
     * 是否为docker环境
     *
     * @return
     */
    public boolean isDockerEnv() {
        return EdgeServiceConstant.value_env_type_docker.equals(this.getOSInfo().get(EdgeServiceConstant.filed_env_type));
    }

    public String getEnvType() {
        return (String) this.getOSInfo().get(EdgeServiceConstant.filed_env_type);
    }

    public void testDockerEnv() {
        if (this.isDockerEnv()) {
            throw new ServiceException("运行环境为docker，该环境不支持该操作！");
        }
    }
}
