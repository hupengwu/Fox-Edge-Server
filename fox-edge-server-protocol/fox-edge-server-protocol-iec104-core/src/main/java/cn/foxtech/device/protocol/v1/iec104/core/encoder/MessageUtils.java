/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.encoder;

import cn.foxtech.device.protocol.v1.iec104.core.enums.AsduTypeIdEnum;
import cn.foxtech.device.protocol.v1.iec104.core.enums.CotReasonEnum;

public class MessageUtils {
    public static String getTypeIdMessage(int typeId) {
        AsduTypeIdEnum typeIdEnum = AsduTypeIdEnum.getEnum(typeId);
        if (typeIdEnum == null) {
            return "未知的Asdu Type Id：" + typeId;
        }

        return typeIdEnum.getMsg();
    }

    public static String getReasonMessage(int reasonId) {
        CotReasonEnum reasonEnum = CotReasonEnum.getEnum(reasonId);
        if (reasonEnum == null) {
            return "未知的Asdu Cot reason Id：" + reasonId;
        }

        return reasonEnum.getMsg();
    }


}
