package cn.foxtech.device.domain.vo;

import cn.foxtech.common.constant.HttpStatus;
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
public class TaskRespondVO extends TaskVO {
    /**
     * 操作参数:一组有序的设备请求
     */
    private List<OperateRespondVO> respondVOS = new ArrayList<>();

    /**
     * 出错代码
     */
    private Integer code = HttpStatus.SUCCESS;

    /**
     * 出错提示信息
     */
    private String msg = "";

    public static TaskRespondVO error(int code, String msg) {
        TaskRespondVO vo = new TaskRespondVO();
        vo.setCode(code);
        vo.setMsg(msg);
        return vo;
    }

    public static TaskRespondVO error(String msg) {
        return TaskRespondVO.error(HttpStatus.ERROR, msg);
    }

    /**
     * 构造单个操作的响应包
     *
     * @param operateRespondVO 操作响应
     * @param clientName
     * @return
     */
    public static TaskRespondVO buildRespondVO(OperateRespondVO operateRespondVO, String clientName) {
        TaskRespondVO taskRespondVO = new TaskRespondVO();
        taskRespondVO.setUuid(operateRespondVO.getUuid());
        taskRespondVO.setClientName(clientName);
        taskRespondVO.setTimeout(operateRespondVO.getTimeout());
        taskRespondVO.getRespondVOS().add(operateRespondVO);
        return taskRespondVO;
    }

    public static TaskRespondVO buildRespondVO(Map<String, Object> map) {
        TaskRespondVO taskRespondVO = new TaskRespondVO();
        taskRespondVO.bindBaseVO(map);

        taskRespondVO.setMsg((String) map.get("msg"));
        taskRespondVO.setCode((Integer) map.get("code"));

        List<Map<String, Object>> respondVOS = (List<Map<String, Object>>) map.get("respondVOS");
        for (Map<String, Object> respondMap : respondVOS) {
            OperateRespondVO respondVO = OperateRespondVO.buildOperateRespondVO(respondMap);

            taskRespondVO.getRespondVOS().add(respondVO);
        }

        return taskRespondVO;
    }

}
