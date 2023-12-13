package cn.foxtech.iot.whzktl.service.scheduler;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.common.service.EntityManageService;
import cn.foxtech.iot.whzktl.service.service.WhZktlIotService;
import cn.foxtech.iot.whzktl.service.vo.DeviceValueNotifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DeviceValuePushScheduler extends PeriodTaskService {
    @Autowired
    private RemoteMqttService remoteMqttService;

    @Autowired
    private WhZktlIotService whZktlIotService;

    @Autowired
    private EntityManageService entityManageService;

    @Override
    public void execute(long threadId) throws Exception {
        // 弹出全部数据
        List<Object> voList = SyncQueueObjectMap.inst().popup(DeviceValueNotifyVO.class.getSimpleName(), false, 1000);

        // 分拆为10个数据为一组
        List<List<Object>> lists = SplitUtils.split(voList, 10);

        // 分批发送
        for (List<Object> list : lists) {
            List<Map<String, Object>> mapList = this.extendDeviceParam(list);
            String body = JsonUtils.buildJson(mapList);
            this.remoteMqttService.getClient().publish(this.whZktlIotService.getPublish() + "/device/value", body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private List<Map<String, Object>> extendDeviceParam(List<Object> entityList) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Object object : entityList) {
            DeviceValueNotifyVO deviceValueNotifyVO = (DeviceValueNotifyVO) object;

            Map<String, Object> result = BeanMapUtils.objectToMap(deviceValueNotifyVO);
            resultList.add(result);

            if (deviceValueNotifyVO.getEntity() == null) {
                continue;
            }

            Map<String, Object> map = BeanMapUtils.objectToMap(deviceValueNotifyVO.getEntity());
            result.put("entity", map);

            DeviceEntity exist = this.entityManageService.getEntity(deviceValueNotifyVO.getEntity().makeServiceKey(), DeviceEntity.class);
            if (exist == null) {
                continue;
            }

            map.put("deviceParam", exist.getDeviceParam());
            map.put("extendParam", exist.getExtendParam());
        }

        return resultList;
    }
}
