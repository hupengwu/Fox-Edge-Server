package cn.foxtech.common.domain.vo;

import cn.foxtech.common.constant.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * restful风格的接口
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RestFulRespondVO extends RestFulVO {
    /**
     * 出错信息
     */
    private String msg = "";

    /**
     * 出错代码
     */
    private Integer code = HttpStatus.SUCCESS;

    public static RestFulRespondVO success(String msg, RestFulVO data) {
        RestFulRespondVO vo = new RestFulRespondVO();
        vo.bindResVO(data);
        vo.code = HttpStatus.SUCCESS;
        vo.msg = msg;
        return vo;
    }

    public static RestFulRespondVO success(RestFulVO data) {
        return RestFulRespondVO.success("", data);
    }

    public static RestFulRespondVO error(int code, String msg) {
        RestFulRespondVO vo = new RestFulRespondVO();
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static RestFulRespondVO error(RestFulVO data, int code, String msg) {
        RestFulRespondVO vo = new RestFulRespondVO();
        vo.bindResVO(data);
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static RestFulRespondVO error(RestFulVO data, String msg) {
        return RestFulRespondVO.error(data, HttpStatus.ERROR, msg);
    }

    public static RestFulRespondVO error(String msg) {
        return RestFulRespondVO.error(HttpStatus.ERROR, msg);
    }
}
