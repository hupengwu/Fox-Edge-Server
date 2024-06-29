package cn.foxtech.device.domain.vo;

import java.util.Map;

/**
 * 单步操作VO
 */
public class OperateRequestVO extends OperateVO {
    public static OperateRequestVO buildOperateRequestVO(Map<String, Object> map) {
        OperateRequestVO vo = new OperateRequestVO();
        vo.bindBaseVO(map);

        return vo;
    }

}
