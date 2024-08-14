/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.uuid;

import java.util.UUID;

public class UuidUtils {
    static public String randomUUID(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
