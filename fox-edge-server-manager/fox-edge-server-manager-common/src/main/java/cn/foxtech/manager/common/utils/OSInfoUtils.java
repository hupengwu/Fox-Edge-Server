package cn.foxtech.manager.common.utils;

import cn.foxtech.common.utils.shell.ShellUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSInfoUtils {
    public static void main(String[] args) {
        List<String> shellLineList = new ArrayList<>();
        shellLineList.add("%Cpu(s): 52.5 us, 22.5 sy,  0.0 ni, 17.5 id,  2.5 wa,  0.0 hi,  5.0 si,  0.0 st");
        OSInfoUtils.getCpuInfo(shellLineList);
    }

    public static Map<String, Object> getCpuInfo() {
        try {
            // 命令行获得操作系统信息
            List<String> shellLineList = ShellUtils.executeShell("top -b -n 1 | grep %Cpu");

            // 解析文本
            return getCpuInfo(shellLineList);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private static Map<String, Object> getCpuInfo(List<String> shellLineList) {
        Map<String, Object> map = new HashMap<>();
        if (shellLineList.isEmpty()) {
            return map;

        }

        String shellLine = shellLineList.get(0);
        String[] items = shellLine.split("\\s+");
        if (items.length < 17) {
            return map;
        }

        // top -b -n 1 | grep %Cpu返回的格式
        map.put("us", makeNumber(items[1]));
        map.put("sy", makeNumber(items[3]));
        map.put("ni", makeNumber(items[5]));
        map.put("id", makeNumber(items[7]));
        map.put("wa", makeNumber(items[9]));
        map.put("hi", makeNumber(items[11]));
        map.put("si", makeNumber(items[13]));
        map.put("st", makeNumber(items[15]));

        return map;
    }

    public static Map<String, Object> getDiskInfo() {
        try {
            // 命令行获得操作系统信息
            List<String> shellLineList = ShellUtils.executeShell("df -h");
            return getDiskInfo(shellLineList);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static Map<String, Object> getDiskInfo(List<String> shellLineList) {
        // 删除第一行信息
        if (!shellLineList.isEmpty()) {
            shellLineList.remove(0);
        }

        double maxSize = 0;
        Map<String, Object> maxMap = null;
        for (String shellLine : shellLineList) {
            String[] items = shellLine.split("\\s+");
            if (items.length < 6) {
                continue;
            }

            // df -h返回的格式
            Map<String, Object> map = new HashMap<>();
            map.put("filesystem", items[0]);
            map.put("sizeTxt", items[1]);
            map.put("size", makeNumber(items[1]));
            map.put("used", makeNumber(items[2]));
            map.put("avail", makeNumber(items[3]));
            map.put("usePercentage", makeNumber(items[4]));
            map.put("mounted", items[5]);

            // 找出最大的磁盘
            double thisSize = (Double) map.get("size");
            if (thisSize > maxSize) {
                maxSize = thisSize;
                maxMap = map;
            }
        }

        return maxMap;

    }

    public static Map<String, Object> getMemInfo() {
        try {
            // 命令行获得操作系统信息
            List<String> shellLineList = ShellUtils.executeShell("free -h");
            return getMemInfo(shellLineList);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static Map<String, Object> getMemInfo(List<String> shellLineList) {
        String[] menItems = shellLineList.get(1).split("\\s+");
        Map<String, Object> map = new HashMap<>();
        map.put("ramTotalTxt", menItems[1]);
        map.put("ramTotal", makeNumber(menItems[1]));
        map.put("ramUsed", makeNumber(menItems[2]));
        map.put("ramFree", makeNumber(menItems[3]));
        map.put("ramShared", makeNumber(menItems[4]));
        map.put("ramBuffCache", makeNumber(menItems[5]));
        map.put("ramAvailable", makeNumber(menItems[6]));

        String[] swapItems = shellLineList.get(2).split("\\s+");
        map.put("swapTotalTxt", swapItems[1]);
        map.put("swapTotal", makeNumber(swapItems[1]));
        map.put("swapUsed", makeNumber(swapItems[2]));
        map.put("swapFree", makeNumber(swapItems[3]));

        return map;
    }

    private static double makeNumber(String size) {
        if (size == null || size.isEmpty()) {
            return 0.0;
        }

        String data = size.toUpperCase();
        if (data.endsWith("%")) {
            data = size.substring(0, data.length() - 1);
            return Double.parseDouble(data);
        }
        if (data.endsWith("B")) {
            data = size.substring(0, data.length() - 1);
            return Double.parseDouble(data);
        }
        if (data.endsWith("BI")) {
            data = size.substring(0, data.length() - 2);
            return Double.parseDouble(data);
        }
        if (data.endsWith("K")) {
            data = size.substring(0, data.length() - 1);
            return Double.parseDouble(data) * 1024;
        }
        if (data.endsWith("KI")) {
            data = size.substring(0, data.length() - 2);
            return Double.parseDouble(data) * 1024;
        }
        if (data.endsWith("M")) {
            data = size.substring(0, data.length() - 1);
            return Double.parseDouble(data) * 1024 * 1024;
        }
        if (data.endsWith("MI")) {
            data = size.substring(0, data.length() - 2);
            return Double.parseDouble(data) * 1024 * 1024;
        }
        if (data.endsWith("G")) {
            data = size.substring(0, data.length() - 1);
            return Double.parseDouble(data) * 1024 * 1024 * 1024;
        }
        if (data.endsWith("GI")) {
            data = size.substring(0, data.length() - 2);
            return Double.parseDouble(data) * 1024 * 1024 * 1024;
        }

        return Double.parseDouble(data);
    }
}
