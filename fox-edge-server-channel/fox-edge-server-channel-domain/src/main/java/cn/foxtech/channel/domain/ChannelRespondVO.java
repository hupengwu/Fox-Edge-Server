package cn.foxtech.channel.domain;

import cn.foxtech.common.constant.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ChannelRespondVO extends ChannelBaseVO {
    /**
     * 出错信息
     */
    private String msg = "";
    /**
     * 出错代码
     */
    private Integer code = HttpStatus.SUCCESS;

    public static ChannelRespondVO buildVO(Map<String, Object> map) {
        ChannelRespondVO vo = new ChannelRespondVO();
        vo.bindBaseVO(map);
        vo.msg = (String) map.get("msg");
        vo.code = (Integer) map.get("code");

        return vo;
    }

    public static ChannelRespondVO success(String msg, ChannelBaseVO data) {
        ChannelRespondVO vo = new ChannelRespondVO();
        vo.bindBaseVO(data);
        vo.code = HttpStatus.SUCCESS;
        vo.msg = msg;
        return vo;
    }

    public static ChannelRespondVO success(ChannelBaseVO data) {
        return ChannelRespondVO.success("", data);
    }

    public static ChannelRespondVO error(int code, String msg) {
        ChannelRespondVO vo = new ChannelRespondVO();
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static ChannelRespondVO error(ChannelBaseVO data, int code, String msg) {
        ChannelRespondVO vo = new ChannelRespondVO();
        vo.bindBaseVO(data);
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

    public static ChannelRespondVO error(ChannelBaseVO data, String msg) {
        return ChannelRespondVO.error(data, HttpStatus.ERROR, msg);
    }

    public static ChannelRespondVO error(String msg) {
        return ChannelRespondVO.error(HttpStatus.ERROR, msg);
    }
}
