package cn.foxtech.common.utils.osinfo.demo;

import cn.foxtech.common.utils.osinfo.OSInfoUtils;

public class Demo {
    public static void main(String[] args) {
        String result = "";
        result = OSInfoUtils.getOSName();
        result =  OSInfoUtils.getCPUID();
        result =  OSInfoUtils.getMAC();
        result =  OSInfoUtils.getMainBordId();
        result = OSInfoUtils.getOSName();
    }
}
