package cn.foxtech.common.utils.test;

import cn.foxtech.common.utils.osinfo.OSInfoUtils;

public class TestOSInfoUtils {
    public static void main(String[] args) {
        String result = "";
        result = OSInfoUtils.getOSName();
        result =  OSInfoUtils.getCPUID();
        result =  OSInfoUtils.getMAC();
        result =  OSInfoUtils.getMainBordId();
        result = OSInfoUtils.getOSName();
    }
}
