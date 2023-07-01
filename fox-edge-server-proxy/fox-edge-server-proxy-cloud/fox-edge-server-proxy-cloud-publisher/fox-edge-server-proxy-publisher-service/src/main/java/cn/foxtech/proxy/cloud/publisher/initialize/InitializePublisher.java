package cn.foxtech.proxy.cloud.publisher.initialize;


import cn.foxtech.common.entity.manager.EntityConfigManager;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.proxy.cloud.publisher.ConfigEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.DefineEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.RecordEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.ValueEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.scheduler.CloudEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.service.CloudEntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Map;

/**
 * 初始化
 */
@Component
public class InitializePublisher {
    @Autowired
    private CloudEntityManageService entityManageService;

    @Autowired
    private CloudEntityManageScheduler entityManageScheduler;


    @Autowired
    private ConfigEntityManageScheduler configEntityManageScheduler;


    @Autowired
    private RecordEntityManageScheduler recordEntityManageScheduler;

    @Autowired
    private DefineEntityManageScheduler defineEntityManageScheduler;

    @Autowired
    private ValueEntityManageScheduler valueEntityManageScheduler;

    @Autowired
    private EntityConfigManager entityConfigManager;


    public void initialize() {
        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动同步线程
        this.entityManageScheduler.schedule();

        // 启动同步线程
        this.configEntityManageScheduler.schedule();

        this.recordEntityManageScheduler.schedule();

        this.defineEntityManageScheduler.schedule();

        this.valueEntityManageScheduler.schedule();

        this.initializeConfig();
    }


    private void initializeConfig() {
        try {
            File file = ResourceUtils.getFile("classpath:config.json");
            String json = FileTextUtils.readTextFile(file);
            Map<String, Object> map = JsonUtils.buildObject(json, Map.class);

            this.entityConfigManager.setConfigEntity("cloudService", map);
        } catch (Exception e) {
            return;
        }
    }
}
