package cn.foxtech.device.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 包操作：批量操作
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TaskVO {
    /**
     * UUID
     */
    private String uuid;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 通信超时
     */
    private Integer timeout;

    public void bindBaseVO(TaskVO vo) {
        this.uuid = vo.uuid;
        this.clientName = vo.clientName;
        this.timeout = vo.timeout;
    }

    public void bindBaseVO(Map<String, Object> map) {
        this.uuid = (String) map.get("uuid");
        this.clientName = (String) map.get("clientName");
        this.timeout = (Integer) map.get("timeout");
    }
}
