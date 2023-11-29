package cn.foxtech.iot.whzktl.service.service;

import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.whzktl.service.vo.DeviceValueNotifyVO;
import cn.foxtech.iot.whzktl.service.whzktl.WhZktlIotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.foxtech.common.utils.SplitUtils.split;

@Component
public class DevicePushScheduler extends PeriodTaskService {
    @Autowired
    private RemoteMqttService remoteMqttService;

    @Autowired
    private WhZktlIotService whZktlIotService;

    @Override
    public void execute(long threadId) throws Exception {
        // 弹出全部数据
        List<Object> voList = SyncQueueObjectMap.inst().popup(DeviceValueNotifyVO.class.getSimpleName(), false, 1000);

        // 分拆为10个数据为一组
        List<List<Object>> lists = SplitUtils.split(voList,10);

        // 分批发送
        for (List<Object> list : lists) {
            String body = JsonUtils.buildJson(list);
            this.remoteMqttService.getClient().publish(this.whZktlIotService.getPublish(), body.getBytes(StandardCharsets.UTF_8));
        }
    }
}
