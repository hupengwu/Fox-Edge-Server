package cn.foxtech.kernel.system.service.vo;

import cn.foxtech.common.constant.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PublishRespondVO {
    private String msg = "";
    private Integer code = HttpStatus.SUCCESS;
    private Map<String, Object> data = new HashMap<>();
}
