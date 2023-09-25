package cn.foxtech.link.domain;

import cn.foxtech.common.constant.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class LinkRespondVO extends LinkBaseVO {
    /**
     * 出错信息
     */
    private String msg = "";
    /**
     * 出错代码
     */
    private Integer code = HttpStatus.SUCCESS;

    public static LinkRespondVO success(String msg, LinkBaseVO data) {
        LinkRespondVO vo = new LinkRespondVO();
        vo.bindBaseVO(data);
        vo.code = HttpStatus.SUCCESS;
        vo.msg = msg;
        return vo;
    }

    public static LinkRespondVO success(LinkBaseVO data) {
        return LinkRespondVO.success("", data);
    }

    public static LinkRespondVO error(int code, String msg) {
        LinkRespondVO vo = new LinkRespondVO();
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static LinkRespondVO error(LinkBaseVO data, int code, String msg) {
        LinkRespondVO vo = new LinkRespondVO();
        vo.bindBaseVO(data);
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static LinkRespondVO error(LinkBaseVO data, String msg) {
        return LinkRespondVO.error(data, HttpStatus.ERROR, msg);
    }

    public static LinkRespondVO error(String msg) {
        return LinkRespondVO.error(HttpStatus.ERROR, msg);
    }
}
