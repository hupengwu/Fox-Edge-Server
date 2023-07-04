package cn.foxtech.common.utils.osinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OSInfoUtils {
    /**
     * 获取当前操作系统名称
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static String getMainBordId() {
        if (OSInfo.isWindows()) {
            return getMainBordId_windows();
        }
        if (OSInfo.isLinux()) {
            return getMainBordId_linux();
        }
        return "";
    }

    // 主板序列号 windows
    private static String getMainBordId_windows() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.delete();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            //  logger.error("获取主板信息错误", e);
        }
        return result.trim();
    }

    // 主板序列号 linux
    private static String getMainBordId_linux() {

        String result = "";
        String maniBord_cmd = "dmidecode | grep 'Serial Number' | awk '{print $3}' | tail -1";
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", maniBord_cmd});// 管道
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
                break;
            }
            br.close();
        } catch (IOException e) {
            //  logger.error("获取主板信息错误", e);
        }
        return result;
    }

    public static String getMAC() {
        if (OSInfo.isWindows()) {
            return getMAC_windows();
        }
        if (OSInfo.isLinux()) {
            return getMAC_linux();
        }
        return "";
    }

    /**
     * 获取mac地址 （如果Linux下有eth0这个网卡）
     */
    private static String getMAC_linux() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // linux下的命令，一般取eth0作为本地主网卡
            process = Runtime.getRuntime().exec("ifconfig eth0");
            // 显示信息中包含有mac地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                // 寻找标示字符串[hwaddr]
                index = line.toLowerCase().indexOf("hwaddr");
                if (index >= 0) {// 找到了
                    // 取出mac地址并去除2边空格
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            //   logger.error("获取mac信息错误", e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                //    logger.error("获取mac信息错误", e1);
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    /*
     * 获取Linux的mac
     */
    private static String getMAC_linuxs() {

        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // linux下的命令，一般取eth0作为本地主网卡
            process = Runtime.getRuntime().exec("ifconfig");
            // 显示信息中包含有mac地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                Pattern pat = Pattern.compile("\\b\\w+:\\w+:\\w+:\\w+:\\w+:\\w+\\b");
                Matcher mat = pat.matcher(line);
                if (mat.find()) {
                    mac = mat.group(0);
                }
            }

        } catch (IOException e) {
            //   logger.error("获取mac信息错误", e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                //     logger.error("获取mac信息错误", e1);
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }


    /**
     * 获取widnows网卡的mac地址.
     */
    private static String getMAC_windows() {
        InetAddress ip = null;
        NetworkInterface ni = null;
        List<String> macList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
                    .getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                ni = (NetworkInterface) netInterfaces.nextElement();
                // ----------特定情况，可以考虑用ni.getName判断
                // 遍历所有ip
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = (InetAddress) ips.nextElement();
                    if (!ip.isLoopbackAddress() // 非127.0.0.1
                            && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                        macList.add(getMacFromBytes(ni.getHardwareAddress()));
                    }
                }
            }
        } catch (Exception e) {
            //   logger.error("获取mac错误", e);
        }
        if (macList.size() > 0) {
            return macList.get(0);
        } else {
            return "";
        }

    }

    private static String getMacFromBytes(byte[] bytes) {
        StringBuffer mac = new StringBuffer();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toUpperCase();
    }

    public static String getCPUID() {
        if (OSInfo.isWindows()) {
            return getCPUID_Windows().replace(" ","");
        }
        if (OSInfo.isLinux()) {
            try {
                return getCPUID_linux().replace(" ","");
            } catch (InterruptedException e) {
                return "";
            }
        }
        return "";
    }

    /**
     * 获取CPU序列号 Windows
     *
     * @return
     */
    private static String getCPUID_Windows() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.delete();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            //   logger.error("获取cpu信息错误", e);
        }
        return result.trim();
    }

    /**
     * 获取CPU序列号 linux
     *
     * @return
     */
    private static String getCPUID_linux() throws InterruptedException {
        String result = "";
        String maniBord_cmd = "dmidecode -t 4 | grep ID |sort -u |awk -F': ' '{print $2}'";
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", maniBord_cmd});// 管道
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
                break;
            }
            br.close();
        } catch (IOException e) {
            //  logger.error("获取主板信息错误", e);
        }
        return result.replace(" ", "");
    }

}
