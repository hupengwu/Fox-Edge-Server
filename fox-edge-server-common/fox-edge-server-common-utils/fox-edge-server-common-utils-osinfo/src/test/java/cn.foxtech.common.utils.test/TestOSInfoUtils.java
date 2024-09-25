/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.test;

import cn.foxtech.common.utils.osinfo.OSInfoUtils;

public class TestOSInfoUtils {
    public static void main(String[] args) {
        String result = "";
        result = OSInfoUtils.getArch();
        result = OSInfoUtils.getOSName();
        result =  OSInfoUtils.getCPUID();
        result =  OSInfoUtils.getMAC();
        result =  OSInfoUtils.getMainBordId();
        result = OSInfoUtils.getOSName();
    }
}
