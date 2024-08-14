/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

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
