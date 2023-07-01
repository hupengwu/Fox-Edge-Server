package cn.foxtech.device.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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
}
