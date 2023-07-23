package cn.foxtech.manager.common.service;


import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 边缘节点的信息：实际上只有CPUID信息
 */
@Component
public class EdgeService {
    private Map<String, Object> osinfo = null;

    /**
     * 获取主机信息：目前测试只有CPU的ID是可以成功获取的
     * CPU的ID能够获取，是因为CPU就英特尔和AMD在生产
     * 至于主板信息，工控机的生产厂家才懒的写
     * 至于网卡，各个计算机的网卡千差万别
     *
     * @return
     */
    public Map<String, Object> getOSInfo() {
        if (this.osinfo == null) {
            Map<String, Object> info = new HashMap<>();
            info.put("cpuid", OSInfoUtils.getCPUID());
            this.osinfo = info;
        }

        return this.osinfo;
    }

    /**
     * CPU的ID
     *
     * @return
     */
    public String getCPUID() {
        return this.getOSInfo().get("cpuid").toString();
    }
}
