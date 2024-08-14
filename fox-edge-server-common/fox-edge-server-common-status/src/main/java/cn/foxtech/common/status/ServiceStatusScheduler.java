/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.status;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 进程状态调度器：把进程状态周期性的刷新到redis
 */
@Component
public class ServiceStatusScheduler extends PeriodTaskService {
    @Autowired
    private ServiceStatus serviceStatus;

    @Autowired
    private AbstractEnvironment environment;

    @Autowired
    private ServiceStatusProducerService producerService;

    @Autowired
    private ServiceStatusConsumerService consumerService;


    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(10 * 1000);

        // 生产数据：将自己的数据发布给其他进程使用
        this.saveProducer();

        this.loadConsumer();
    }

    public void initialize() {
        // 1.初始化消费者：将数据从redis中装载过来，并保存到本地缓存
        this.initConsumer();

        // 2.初始化生产者：将redis的上次数据，再加上基础数据，保存到本地缓存
        this.initProducer();
    }

    private void initConsumer() {
        try {
            Boolean value = this.environment.getProperty(this.serviceStatus.getCacheModeKey(), Boolean.class);
            if (value == null) {
                return;
            }

            this.serviceStatus.setCacheMode(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initProducer() {
        // 在初始化阶段，从redis的数据中，继承原有数据，防止删除/添加造成的抖动现象
        Map<String, Object> value = (Map<String, Object>) MapUtils.getValue(this.consumerService.getStatus(), this.serviceStatus.getServiceKey());
        if (value != null) {
            this.serviceStatus.getProducerData().putAll(value);
        }

        // 填写基本的业务状态
        this.serviceStatus.getProducerData().put(RedisStatusConstant.field_service_type, this.serviceStatus.getFoxServiceType());
        this.serviceStatus.getProducerData().put(RedisStatusConstant.field_service_name, this.serviceStatus.getFoxServiceName());
        this.serviceStatus.getProducerData().put(RedisStatusConstant.field_model_type, this.serviceStatus.getFoxModelType());
        this.serviceStatus.getProducerData().put(RedisStatusConstant.field_model_name, this.serviceStatus.getFoxModelName());
        this.serviceStatus.getProducerData().put(RedisStatusConstant.field_application_name, this.producerService.getApplicationName());

        // 填写常见属性
        this.setProducerFromProperty(ServiceVOFieldConstant.field_app_port, "server.port", Integer.class);
    }

    /**
     * 将yaml中的数据，保存到redis
     */
    public <T> void setProducerFromProperty(String statusKey, String propertyKey, Class<T> targetType) {
        try {
            T value = this.environment.getProperty(propertyKey, targetType);
            if (value == null) {
                return;
            }

            this.serviceStatus.getProducerData().put(statusKey, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProducer() {
        // 模块信息
        this.serviceStatus.getProducerData().put(RedisStatusConstant.field_active_time, System.currentTimeMillis());

        this.producerService.setData(this.serviceStatus.getProducerData());

        // 保存数据
        this.producerService.save(this.serviceStatus.getServiceKey());
    }

    public void loadConsumer() {
        if (!this.serviceStatus.isCacheMode()) {
            return;
        }

        this.consumerService.load();
    }
}
