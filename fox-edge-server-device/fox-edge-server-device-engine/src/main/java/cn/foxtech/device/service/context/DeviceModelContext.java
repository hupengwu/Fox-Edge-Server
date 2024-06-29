package cn.foxtech.device.service.context;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.core.context.IApplicationContext;
import cn.foxtech.device.service.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备模板的上下文信息：为静态解码器，提供必要的全局信息
 */
@Component
public class DeviceModelContext implements IApplicationContext {
    private final Map<String, Object> context = new ConcurrentHashMap<>();

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private DeviceTemplateNotify typeNotify;

    public void initialize() {
        ApplicationContext.initialize(this);

        Map<String, Object> deviceTemplateContext = this.context;

        // 装置数据
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceModelEntity.class);
        for (BaseEntity entity : entityList) {
            DeviceModelEntity deviceTemplateEntity = (DeviceModelEntity) entity;

            // 通过JSON的转换，达到变相克隆一个对象的目的，避免消费者破坏entityManageService的内部数据
            String json = JsonUtils.buildJsonWithoutException(deviceTemplateEntity);
            Map<String, Object> deviceTemplateMap = JsonUtils.buildMapWithDefault(json, new ConcurrentHashMap<>());
            deviceTemplateContext.put(deviceTemplateEntity.makeServiceKey(), deviceTemplateMap);
        }


        // 给notify绑定context，后续context数据由notify根据变更，进行修改
        this.typeNotify.bindContext(this.context);

        // 将notify绑定到redis上，接收它的通知
        ConsumerRedisService consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(DeviceModelEntity.class);
        consumerRedisService.bind(this.typeNotify);
    }

    /**
     * 获得设备模型
     *
     * @return 全量的设备模型
     */
    public Map<String, Object> getDeviceModels(String modelName) {
        DeviceModelEntity deviceTemplateEntity = new DeviceModelEntity();
        deviceTemplateEntity.setModelName(modelName);

        Map<String, Object> deviceTemplateContext = this.context;
        Map<String, Object> deviceTemplate = (Map<String, Object>) deviceTemplateContext.get(deviceTemplateEntity.makeServiceKey());
        return deviceTemplate;
    }

}
