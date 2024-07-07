package cn.foxtech.kernel.system.service.vo;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * RestFul风格的VO
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RestfulLikeVO {
    /**
     * topic
     */
    private String topic;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 通道类型
     */
    private String resource;

    /**
     * 通道名称
     */
    private String method;
    /**
     * 发送数据
     */
    private Object body;

    public void bindVO(RestfulLikeVO other) {
        this.topic = other.topic;
        this.uuid = other.uuid;
        this.resource = other.resource;
        this.method = other.method;
        this.body = other.body;
    }
}
