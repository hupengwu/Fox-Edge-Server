package cn.foxtech.common.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RestFulVO {
    /**
     * UUID
     */
    private String uuid;

    /**
     * 命令类型
     */
    private String method;

    /**
     * 模块类型
     */
    private String uri;

    /**
     * 通道名称
     */
    private Object data;

    public void bindResVO(RestFulVO vo) {
        this.method = vo.method;
        this.uuid = vo.uuid;
        this.uri = vo.uri;
        this.data = vo.data;
    }
}
