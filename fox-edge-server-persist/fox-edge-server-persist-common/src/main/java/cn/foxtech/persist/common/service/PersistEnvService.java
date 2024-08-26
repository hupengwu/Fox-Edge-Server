package cn.foxtech.persist.common.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@Component
public class PersistEnvService {
    /**
     * 是否为组合模式
     */
    private boolean compose = false;

    public String getServerConfig() {
        return this.compose ? "persistServerConfig" : "serverConfig";
    }
}
