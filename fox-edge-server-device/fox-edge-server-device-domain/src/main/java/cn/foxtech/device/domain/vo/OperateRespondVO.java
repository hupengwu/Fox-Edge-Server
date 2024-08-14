/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.domain.vo;


import cn.foxtech.common.constant.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应消息
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateRespondVO extends OperateRequestVO {
    public static final String data_mode = "mode";
    public static final String data_value = "value";
    public static final String data_comm_status = "commStatus";
    public static final String data_comm_status_failed_time = "commFailedTime";
    public static final String data_comm_status_success_time = "commSuccessTime";
    public static final String data_comm_status_failed_count = "commFailedCount";

    /**
     * 返回数据
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 出错代码
     */
    private Integer code = HttpStatus.SUCCESS;

    /**
     * 出错提示信息
     */
    private String msg = "";

    public static Map<String, Object> buildCommonStatus(long commSuccessTime, long commFailedTime, int commFailedCount) {
        Map<String, Object> status = new HashMap<>();
        status.put(data_comm_status_failed_time, commFailedTime);
        status.put(data_comm_status_success_time, commSuccessTime);
        status.put(data_comm_status_failed_count, commFailedCount);
        return status;

    }

    public static OperateRespondVO error(int code, String msg) {
        OperateRespondVO vo = new OperateRespondVO();
        vo.setCode(code);
        vo.setMsg(msg);
        return vo;
    }

    public static OperateRespondVO error(String msg) {
        return OperateRespondVO.error(HttpStatus.ERROR, msg);
    }

    public void setData(Map<String, Object> valueStatus, Map<String, Object> commStatus) {
        this.getData().put(OperateRespondVO.data_value, valueStatus);
        this.getData().put(OperateRespondVO.data_comm_status, commStatus);
    }

    public void bind(OperateRespondVO other) {
        this.bindBaseVO(other);
        this.setData(other.data);
        this.setMsg(other.msg);
        this.setCode(other.code);
    }

    public static OperateRespondVO buildOperateRespondVO(Map<String,Object> map){
        OperateRespondVO operateRespondVO = new OperateRespondVO();
        operateRespondVO.bindBaseVO(map);

        operateRespondVO.setCode((Integer)map.get("code"));
        operateRespondVO.setMsg((String)map.get("msg"));
        operateRespondVO.setData((Map<String,Object>)map.get("data"));

        return operateRespondVO;
    }
}
