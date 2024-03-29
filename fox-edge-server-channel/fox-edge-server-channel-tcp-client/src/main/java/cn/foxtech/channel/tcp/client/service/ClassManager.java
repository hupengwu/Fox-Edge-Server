package cn.foxtech.channel.tcp.client.service;

import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * java的Class管理器
 */
@Component
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ClassManager {
    /**
     * 解码器列表
     */
    private Set<Class<?>> classSet = new CopyOnWriteArraySet<>();

    public Class<?> getSplitMessageHandler(String className) {
        for (Class<?> aClass : this.classSet) {
            String name = aClass.getName();

            if (!SplitMessageHandler.class.isAssignableFrom(aClass)) {
                continue;
            }

            if (!name.equals(className)) {
                continue;
            }

            return aClass;
        }

        return null;
    }

    public Class<?> getServiceKeyHandler(String className) {
        for (Class<?> aClass : this.classSet) {
            String name = aClass.getName();

            if (!ServiceKeyHandler.class.isAssignableFrom(aClass)) {
                continue;
            }

            if (!name.equals(className)) {
                continue;
            }

            return aClass;
        }

        return null;
    }


}
