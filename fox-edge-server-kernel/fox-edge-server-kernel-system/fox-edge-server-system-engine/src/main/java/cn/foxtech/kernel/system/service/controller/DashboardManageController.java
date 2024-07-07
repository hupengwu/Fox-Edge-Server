package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.DeviceStatusEntity;
import cn.foxtech.common.entity.service.device.DeviceEntityService;
import cn.foxtech.common.entity.service.deviceobject.DeviceObjectEntityMapper;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.common.utils.OSInfoUtils;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/dashboard")
public class DashboardManageController {
    @Autowired
    protected DeviceEntityService deviceEntityService;
    @Autowired
    private ServiceStatus serviceStatus;
    @Autowired
    private EntityManageService entityManageService;
    @Autowired
    private DeviceObjectEntityMapper objectEntityMapper;

    @Autowired
    private EdgeService edgeService;


    /**
     * 查询一级列表
     *
     * @param params 用戶参数
     * @return 查询结果
     */
    @PostMapping("indicator")
    public AjaxResult queryIndicator(@RequestBody Map<String, Object> params) {
        try {
            Map<String, Object> indicator = new HashMap<>();
            this.getChannelEntityIndicator(indicator);
            this.getObjectEntityIndicator(indicator);
            this.getDeviceStatusEntityIndicator(indicator);
            this.getDeviceEntityIndicator(indicator);
            this.getServiceEntityIndicator(indicator);
            this.getEdgeIndicator(indicator);

            // 不正确的参数
            return AjaxResult.success(indicator);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private void getDeviceEntityIndicator(Map<String, Object> indicators) {
        Long deviceTotal = this.deviceEntityService.selectEntityListCount(null);
        List deviceType = this.deviceEntityService.selectEntityListDistinct("device_type");


        indicators.put("deviceTotalCount", deviceTotal);
        indicators.put("deviceTypeCount", deviceType.size());
    }

    private void getDeviceStatusEntityIndicator(Map<String, Object> indicators) throws JsonParseException {
        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceStatusEntity.class);
        Map<String, Object> dataMap = redisReader.readHashMap();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failedCount = new AtomicInteger();

        for (String key : dataMap.keySet()) {
            Object data = dataMap.get(key);
            if (!(data instanceof Map)) {
                continue;
            }

            Map<String, Object> map = (Map<String, Object>) data;
            Integer commFailedCount = (Integer) map.getOrDefault("commFailedCount", 0);

            if (commFailedCount > 0) {
                failedCount.getAndIncrement();
            }
            if (commFailedCount == 0) {
                successCount.getAndIncrement();
            }

        }

        indicators.put("deviceFailedCount", failedCount.intValue());
        indicators.put("deviceSuccessCount", successCount.intValue());
    }

    private void getChannelEntityIndicator(Map<String, Object> indicators) {
        AtomicInteger channelTotal = new AtomicInteger();
        Set<String> channelType = new HashSet<>();
        this.entityManageService.getEntityCount(ChannelEntity.class, (Object value) -> {
            ChannelEntity entity = (ChannelEntity) value;
            channelTotal.getAndIncrement();
            channelType.add(entity.getChannelType());
            return true;
        });

        indicators.put("channelTotalCount", channelTotal.intValue());
        indicators.put("channelTypeCount", channelType.size());
    }

    private void getObjectEntityIndicator(Map<String, Object> indicators) {
        // 查询总数
        String selectCount = PageUtils.makeSelectCountSQL("tb_device_object", "");
        Integer total = this.objectEntityMapper.executeSelectCount(selectCount);

        indicators.put("objectTotalCount", total.intValue());
    }

    private void getServiceEntityIndicator(Map<String, Object> indicators) {
        List<Map<String, Object>> resultList = this.serviceStatus.getDataList(60 * 1000);
        indicators.put("serviceActiveCount", resultList.size());
    }

    private void getEdgeIndicator(Map<String, Object> indicators) {
        Map<String, Object> diskInfo = OSInfoUtils.getDiskInfo();
        Map<String, Object> memInfo = OSInfoUtils.getMemInfo();
        Map<String, Object> cpuInfo = OSInfoUtils.getCpuInfo();
        indicators.put("cpuUID", this.edgeService.getCPUID());
        indicators.put("envType", this.edgeService.isDockerEnv());

        indicators.put("diskSizeTxt", diskInfo.get("sizeTxt"));
        indicators.put("diskSize", diskInfo.getOrDefault("size", 1));
        indicators.put("diskUsed", diskInfo.getOrDefault("used", 1));
        indicators.put("diskAvail", diskInfo.getOrDefault("avail", 1));
        indicators.put("diskUsePercentage", diskInfo.getOrDefault("usePercentage", 1));

        indicators.put("ramTotalTxt", memInfo.get("ramTotalTxt"));
        indicators.put("ramTotal", memInfo.getOrDefault("ramTotal", 1));
        indicators.put("ramUsed", memInfo.getOrDefault("ramUsed", 1));
        indicators.put("ramFree", memInfo.getOrDefault("ramFree", 1));

        indicators.put("swapTotalTxt", memInfo.get("swapTotalTxt"));
        indicators.put("swapTotal", memInfo.getOrDefault("swapTotal", 1));
        indicators.put("swapUsed", memInfo.getOrDefault("swapUsed", 1));
        indicators.put("swapFree", memInfo.getOrDefault("swapFree", 1));


        indicators.put("cpuUs", cpuInfo.getOrDefault("us", 1));
        indicators.put("cpuSy", cpuInfo.getOrDefault("sy", 1));
        indicators.put("cpuNi", cpuInfo.getOrDefault("ni", 1));
        indicators.put("cpuId", cpuInfo.getOrDefault("id", 1));
        indicators.put("cpuWa", cpuInfo.getOrDefault("wa", 1));
        indicators.put("cpuHi", cpuInfo.getOrDefault("hi", 1));
        indicators.put("cpuSi", cpuInfo.getOrDefault("si", 1));
        indicators.put("cpuSt", cpuInfo.getOrDefault("st", 1));


        indicators.putAll(memInfo);
    }

}
