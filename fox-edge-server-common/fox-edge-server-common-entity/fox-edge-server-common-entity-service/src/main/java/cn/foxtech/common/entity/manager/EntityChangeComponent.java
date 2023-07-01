package cn.foxtech.common.entity.manager;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知组件：通知数据发生变化
 */
@Data
@Component
public class EntityChangeComponent {
    /**
     * 再次发生重新装载的实体类型：从而感知消费者数据发生了变化
     */
    private Map<String, Long> reloadMap = new ConcurrentHashMap<>();
    /**
     * 再次发生发布的实体类型：从而感知生产者数据发生了变化
     */
    private Map<String, Long> publishMap = new ConcurrentHashMap<>();
}
