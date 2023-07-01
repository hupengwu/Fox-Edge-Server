package cn.foxtech.channel.bacnet.server.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 周期性发现远端设备
 */
@Component
public class SchedulerService {
    @Autowired
    BACnetServerService serverService;

    @Scheduled(fixedDelay = 60 * 1000)
    public void fixedDelayTask() {
        // 定时发现新的设备
        serverService.discoveryRemoteDevice();
    }
}
