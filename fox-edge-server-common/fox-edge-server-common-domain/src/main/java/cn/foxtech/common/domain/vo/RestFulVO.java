package cn.foxtech.common.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * restful风格的消息结构
 * 背景：目的是在HTTP、MQTT、REDIS等多种传输管道中，提供统一的交互数据结构
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RestFulVO {
    /**
     * UUID
     */
    private String uuid;

    /**
     * 方法名稱：例如，POST/PUT/GET/DELETE
     */
    private String method;

    /**
     * URI：能夠提供HTTP服务的资源名称
     */
    private String uri;

    /**
     * 数据：类似HTTP的BODY
     */
    private Object data;

    public void bindResVO(RestFulVO vo) {
        this.method = vo.method;
        this.uuid = vo.uuid;
        this.uri = vo.uri;
        this.data = vo.data;
    }

    public void bindResVO(Map<String, Object> map) {
        this.method = (String) map.get("method");
        this.uuid = (String) map.get("uuid");
        this.uri = (String) map.get("uri");
        this.data = map.get("data");
    }
}
