package cn.foxtech.device.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 单步操作VO
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateVO {
    /**
     * UUID
     */
    private String uuid;
    /**
     * 操作模式
     */
    private String operateMode = "";
    /**
     * 设备名称
     */
    private String deviceName = "";

    /**
     * 设备类型名
     */
    private String deviceType = "";
    /**
     * 操作名称
     */
    private String operateName = "";
    /**
     * 操作参数
     */
    private Map<String, Object> param = new HashMap<>();
    /**
     * 通信超时
     */
    private Integer timeout;
    /**
     * 是否需要记录：默认不需要
     */
    private Boolean record;

    /**
     * 绑定信息：方便将request的信息复制给respond
     *
     * @param vo
     */
    public void bindBaseVO(OperateVO vo) {
        this.uuid = vo.uuid;
        this.deviceName = vo.deviceName;
        this.deviceType = vo.deviceType;
        this.timeout = vo.timeout;
        this.operateMode = vo.operateMode;
        this.operateName = vo.operateName;
        this.param = vo.param;
        this.record = vo.record;
    }
}
