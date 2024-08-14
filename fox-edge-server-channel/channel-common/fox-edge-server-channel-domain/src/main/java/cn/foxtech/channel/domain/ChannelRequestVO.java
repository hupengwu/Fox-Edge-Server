/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.domain;

import java.util.Map;

public class ChannelRequestVO extends ChannelBaseVO {
    public static ChannelRequestVO buildVO(Map<String, Object> map) {
        ChannelRequestVO vo = new ChannelRequestVO();
        vo.bindBaseVO(map);

        return vo;
    }

}
