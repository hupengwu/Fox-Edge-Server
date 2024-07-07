package cn.foxtech.persist.iotdb.initialize;


import cn.foxtech.persist.common.initialize.PersistInitialize;
import cn.foxtech.persist.iotdb.service.IoTDBSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {

    @Autowired
    private PersistInitialize persistInitialize;

    @Autowired
    private IoTDBSessionService iotDBSessionService;

    @Override
    public void run(String... args) {
        this.persistInitialize.initialize();

        this.iotDBSessionService.initialize();
    }
}
