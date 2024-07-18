package cn.foxtech.common.domain.vo;

import cn.foxtech.common.constant.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RestfulLikeRespondVO extends RestfulLikeVO {
    /**
     * 出错信息
     */
    private String msg = "";
    /**
     * 出错代码
     */
    private Integer code = HttpStatus.SUCCESS;

    public static RestfulLikeRespondVO success(String msg, RestfulLikeVO data) {
        RestfulLikeRespondVO vo = new RestfulLikeRespondVO();
        vo.bindVO(data);
        vo.code = HttpStatus.SUCCESS;
        vo.msg = msg;
        return vo;
    }

    public static RestfulLikeRespondVO success(RestfulLikeVO data) {
        return RestfulLikeRespondVO.success("", data);
    }

    public static RestfulLikeRespondVO error(int code, String msg) {
        RestfulLikeRespondVO vo = new RestfulLikeRespondVO();
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static RestfulLikeRespondVO error(RestfulLikeVO data, int code, String msg) {
        RestfulLikeRespondVO vo = new RestfulLikeRespondVO();
        vo.bindVO(data);
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static RestfulLikeRespondVO error(RestfulLikeVO data, String msg) {
        return RestfulLikeRespondVO.error(data, HttpStatus.ERROR, msg);
    }

    public static RestfulLikeRespondVO error(String msg) {
        return RestfulLikeRespondVO.error(HttpStatus.ERROR, msg);
    }

}
