package cn.foxtech.common.domain.vo;

import cn.foxtech.common.constant.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * rpc风格的接口
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PublicRespondVO extends PublicVO {
    /**
     * 出错信息
     */
    private String msg = "";

    /**
     * 出错代码
     */
    private Integer code = HttpStatus.SUCCESS;

    public static PublicRespondVO success(String msg, PublicVO data) {
        PublicRespondVO vo = new PublicRespondVO();
        vo.bindResVO(data);
        vo.code = HttpStatus.SUCCESS;
        vo.msg = msg;
        return vo;
    }

    public static PublicRespondVO success(PublicVO data) {
        return PublicRespondVO.success("", data);
    }

    public static PublicRespondVO error(int code, String msg) {
        PublicRespondVO vo = new PublicRespondVO();
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static PublicRespondVO error(PublicVO data, int code, String msg) {
        PublicRespondVO vo = new PublicRespondVO();
        vo.bindResVO(data);
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static PublicRespondVO error(PublicVO data, String msg) {
        return PublicRespondVO.error(data, HttpStatus.ERROR, msg);
    }

    public static PublicRespondVO error(String msg) {
        return PublicRespondVO.error(HttpStatus.ERROR, msg);
    }
}
