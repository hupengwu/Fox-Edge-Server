/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 包操作：批量操作
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TaskRequestVO extends TaskVO {
    /**
     * 操作参数:一组有序的设备请求
     */
    private List<OperateRequestVO> requestVOS = new ArrayList<>();

    /**
     * 构造单个操作的请求包
     *
     * @param operateRequestVO 操作请求
     * @param clientName
     * @return
     */
    public static TaskRequestVO buildRequestVO(OperateRequestVO operateRequestVO, String clientName) {
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.setUuid(operateRequestVO.getUuid());
        taskRequestVO.setClientName(clientName);
        taskRequestVO.setTimeout(operateRequestVO.getTimeout());
        taskRequestVO.getRequestVOS().add(operateRequestVO);
        return taskRequestVO;
    }

    public static TaskRequestVO buildRequestVO(Map<String, Object> map) {
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.bindBaseVO(map);


        List<Map<String, Object>> requestVOS = (List<Map<String, Object>>) map.get("requestVOS");
        for (Map<String, Object> requestMap : requestVOS) {
            OperateRequestVO requestVO = OperateRequestVO.buildOperateRequestVO(requestMap);

            taskRequestVO.requestVOS.add(requestVO);
        }

        return taskRequestVO;
    }
}
