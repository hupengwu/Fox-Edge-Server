package cn.foxtech.common.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RestfulLikeRequestVO extends RestfulLikeVO {
    /**
     * 请求时间
     */
    private long executeTime = 0L;
}
