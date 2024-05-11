package cn.foxtech.persist.mysql.initialize;


import cn.foxtech.persist.common.initialize.PersistInitialize;
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

    @Override
    public void run(String... args) {
        this.persistInitialize.initialize();
    }
}
