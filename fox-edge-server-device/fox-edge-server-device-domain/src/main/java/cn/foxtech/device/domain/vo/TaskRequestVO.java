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

    public static TaskRequestVO buildRequestVO(Map<String, Object> taskRequestMap) {
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.bindBaseVO(taskRequestMap);

        List<Map<String, Object>> requestVOS = (List<Map<String, Object>>) taskRequestMap.get("requestVOS");
        for (Map<String, Object> requestMap : requestVOS) {
            OperateRequestVO operateRequestVO = new OperateRequestVO();
            operateRequestVO.bindBaseVO(requestMap);

            taskRequestVO.getRequestVOS().add(operateRequestVO);
        }

        return taskRequestVO;
    }
}
