package cn.foxtech.zkturingl.service.scheduler;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.common.service.EntityManageService;
import cn.foxtech.zkturingl.service.service.ZKTuringlService;
import cn.foxtech.zkturingl.service.vo.DeviceValueNotifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class DeviceValuePushScheduler extends PeriodTaskService {
    @Autowired
    private RemoteMqttService remoteMqttService;

    @Autowired
    private ZKTuringlService turinglService;

    @Autowired
    private EntityManageService entityManageService;

    @Override
    public void execute(long threadId) throws Exception {
        // 弹出全部数据
        List<Object> voList = this.notifyDeviceValues();

        // 分拆为10个数据为一组
        List<List<Object>> lists = SplitUtils.split(voList, 10);

        // 分批发送
        for (List<Object> list : lists) {
            List<Map<String, Object>> mapList = this.extendDeviceParam(list);
            String body = JsonUtils.buildJson(mapList);
            this.remoteMqttService.getClient().publish(this.turinglService.getPublish() + "/device/value", body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private List<Object> notifyDeviceValues() {
        List<Object> voList = new ArrayList<>();

        try {
            AgileMapRedisService redisService = this.entityManageService.getAgileMapService(DeviceValueEntity.class.getSimpleName());

            // 装载数据：从redis读取数据，并获知变化状态
            Map<String, BaseEntity> addMap = new HashMap<>();
            Set<String> delSet = new HashSet<>();
            Map<String, BaseEntity> mdyMap = new HashMap<>();
            redisService.loadChangeEntities(addMap, delSet, mdyMap, new DeviceValueEntity());

            // 检测：数据
            if (addMap.isEmpty() && delSet.isEmpty() && mdyMap.isEmpty()) {
                return voList;
            }

            for (String key : addMap.keySet()) {
                DeviceValueNotifyVO vo = new DeviceValueNotifyVO();
                vo.setMethod("insert");
                vo.setEntity(addMap.get(key));

                voList.add(vo);
            }
            for (String key : mdyMap.keySet()) {
                DeviceValueNotifyVO vo = new DeviceValueNotifyVO();
                vo.setMethod("update");
                vo.setEntity(mdyMap.get(key));

                voList.add(vo);
            }
            for (String key : delSet) {
                DeviceValueNotifyVO vo = new DeviceValueNotifyVO();
                vo.setMethod("delete");

                voList.add(vo);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        return voList;
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
