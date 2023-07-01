package cn.foxtech.common.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PublicVO {
    /**
     * 命令类型
     */
    private String cmd;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 模块类型
     */
    private String modelType;

    /**
     * 模块名称
     */
    private String modelName;

    /**
     * 通道名称
     */
    private Object data;

    public void bindResVO(PublicVO vo) {
        this.cmd = vo.cmd;
        this.uuid = vo.uuid;
        this.modelType = vo.modelType;
        this.modelName = vo.modelName;
        this.data = vo.data;
    }
}
