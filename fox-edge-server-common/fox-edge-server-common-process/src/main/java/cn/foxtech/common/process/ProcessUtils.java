/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.process;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.shell.ShellUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;

/**
 * 获得进程的运行信息
 */
public class ProcessUtils {
    public static void extendAppStatus(List<Map<String, Object>> serviceIniFileInfoList) throws IOException, InterruptedException {
        List<Map<String, Object>> processList = getProcess();
        Map<String, Map<String, Object>> pathName2ProcessStatus = ContainerUtils.buildMapByMapAt(processList, ServiceVOFieldConstant.field_path_name, String.class);

        for (Map<String, Object> confFileInfo : serviceIniFileInfoList) {
            String pathName = (String) confFileInfo.get(ServiceVOFieldConstant.field_path_name);
            if (pathName == null || pathName.isEmpty()) {
                continue;
            }


            Map<String, Object> status = pathName2ProcessStatus.get(pathName);
            if (status != null) {
                confFileInfo.putAll(status);
            }
        }
    }

    public static List<Map<String, Object>> getProcess() throws IOException, InterruptedException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.addAll(getProcess(ServiceVOFieldConstant.field_type_kernel));
        resultList.addAll(getProcess(ServiceVOFieldConstant.field_type_system));
        resultList.addAll(getProcess(ServiceVOFieldConstant.field_type_service));
        return resultList;
    }

    /**
     * 获得进程信息
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<Map<String, Object>> getProcess(String appType) throws IOException, InterruptedException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        File file = new File("");
        String path = file.getAbsolutePath() + "/bin/" + appType + "/";
        List<String> shellLineList = ShellUtils.executeShell("ps -aux|grep " + path);
        for (String shellLine : shellLineList) {
            String[] items = shellLine.split("\\s+");
            if (items.length < 14) {
                continue;
            }


            // 剔除掉DEBUG的干扰信息
            items = filterDebug(items);

            // ps -aux返回的格式: 0~10是linux的固定信息项目
            Map<String, Object> map = makeShellParam(items);

            // 检查：该命令是否为java/python/python3命令
            String appEngine = items[10];
            if (!"java".equals(appEngine) && !"python".equals(appEngine) && !"python3".equals(appEngine)) {
                continue;
            }

            // 从命令行信息中取出 用户参数
            List<String> params = splitLinuxShellParam(items);

            // 从fox-edge格式的用户参数中，取出用户参数
            Map<String, Object> foxParam = splitFoxEdgeParam(params, path);
            map.putAll(foxParam);

            map.put(ServiceVOFieldConstant.field_app_type, appType);
            map.put(ServiceVOFieldConstant.field_app_engine, appEngine);

            resultList.add(map);
        }

        return resultList;
    }

    public static Map<String, Object> getSysProcess(String feature) throws IOException, InterruptedException {
        return getSysProcess(feature, true);
    }

    public static Map<String, Object> getSysProcess(String feature, boolean isSysParam) throws IOException, InterruptedException {
        List<String> shellLineList = ShellUtils.executeShell("ps -aux|grep " + feature);
        for (String shellLine : shellLineList) {
            String[] items = shellLine.split("\\s+");
            if (items.length < 11) {
                continue;
            }

            // 剔除掉DEBUG的干扰信息
            items = filterDebug(items);

            // ps -aux返回的格式: 0~10是linux的固定信息项目
            Map<String, Object> map = makeShellParam(items);

            // 检查：是否存在该特征
            if (isSysParam) {
                String command = (String) map.get("command");
                if (command.endsWith(feature)) {
                    return map;
                }
            } else {
                // 非系统参数部分
                for (String item : items) {
                    if (item.indexOf(feature) >= 0) {
                        return map;
                    }
                }
            }

        }

        return null;
    }

    public static Set<Long> getProcessPort(Long pid) throws IOException, InterruptedException {
        List<String> shellLineList = ShellUtils.executeShell("ss -tnlp | grep pid=" + pid);

        Set<Long> ports = new HashSet<>();
        for (String shellLine : shellLineList) {
            String[] items = shellLine.split("\\s+");
            if (items.length < 6) {
                continue;
            }

            // 分拆IP:PORT
            items = items[3].split(":");
            if (items.length < 2) {
                continue;
            }


            ports.add(Long.parseLong(items[items.length - 1]));
        }

        return ports;
    }

    private static Map<String, Object> makeShellParam(String[] items) {
        // ps -aux返回的格式
        // 0~10是linux的固定信息项目
        Map<String, Object> map = new HashMap<>();
        map.put("user", items[0]);
        map.put(ServiceVOFieldConstant.field_pid, Long.parseLong(items[1]));
        map.put("cpu", Double.parseDouble(items[2]));
        map.put("men", Double.parseDouble(items[3]));
        map.put("vsz", Long.parseLong(items[4]));
        map.put(ServiceVOFieldConstant.field_rss, Long.parseLong(items[5]));
        map.put("stime", items[8]);
        map.put("time", items[9]);
        map.put("command", items[10]);
        return map;
    }

    /**
     * 获得loader的进程PID
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static Map<Long, Long> getLoaderPid() throws IOException, InterruptedException {
        // 查询命令行信息
        File file = new File("");
        String path = file.getAbsolutePath() + "/bin/";
        List<String> shellLineList = ShellUtils.executeShell("ps -aux|grep " + path);

        // 提取PID信息
        Map<String, Object> infMap = new HashMap<>();
        for (String shellLine : shellLineList) {
            Map<String, Object> javaInf = findJavaInf(shellLine);
            if (javaInf != null) {
                MapUtils.setValue(infMap, javaInf.get(ServiceVOFieldConstant.field_path_name), ServiceVOFieldConstant.field_file_name, javaInf.get(ServiceVOFieldConstant.field_pid));
                continue;
            }

            Map<String, Object> loadInf = findLoaderInf(shellLine);
            if (loadInf != null) {
                MapUtils.setValue(infMap, loadInf.get(ServiceVOFieldConstant.field_path_name), ServiceVOFieldConstant.field_loader_name, loadInf.get(ServiceVOFieldConstant.field_pid));
                continue;
            }
        }

        Map<Long, Long> result = new HashMap<>();
        for (String key : infMap.keySet()) {
            Map<String, Object> map = (Map<String, Object>) infMap.get(key);
            Long javaPid = (Long) MapUtils.getValue(map, ServiceVOFieldConstant.field_file_name);
            Long loadPid = (Long) MapUtils.getValue(map, ServiceVOFieldConstant.field_loader_name);
            if (javaPid != null && loadPid != null) {
                result.put(javaPid, loadPid);
            }
        }

        return result;
    }

    private static Map<String, Object> findJavaInf(String shellLine) {
        String[] items = shellLine.split("\\s+");
        if (items.length < 14) {
            return null;
        }

        // 剔除掉DEBUG的干扰信息
        items = filterDebug(items);

        // ps -aux返回的格式
        // 0~10是linux的固定信息项目
        Map<String, Object> map = new HashMap<>();
        map.put(ServiceVOFieldConstant.field_pid, Long.parseLong(items[1]));
        if (!"java".equals(items[10])) {
            return null;
        }

        // 从命令行信息中取出 用户参数
        List<String> params = splitLinuxShellParam(items);
        String flag = "/opt/fox-edge/bin/";
        String pathName = findParam(params, flag);
        map.put(ServiceVOFieldConstant.field_path_name, flag + pathName);
        return map;
    }

    private static Map<String, Object> findLoaderInf(String shellLine) {
        String[] items = shellLine.split("\\s+");
        if (items.length < 14) {
            return null;
        }

        // 剔除掉DEBUG的干扰信息
        items = filterDebug(items);

        // ps -aux返回的格式
        // 0~10是linux的固定信息项目
        Map<String, Object> map = new HashMap<>();
        map.put(ServiceVOFieldConstant.field_pid, Long.parseLong(items[1]));
        if ("java".equals(items[10])) {
            return null;
        }

        // loader也是在bin目录
        String flag = "/opt/fox-edge/bin/";
        if (!items[10].startsWith(flag)) {
            return null;
        }

        // 从命令行信息中取出 用户参数
        List<String> params = splitLinuxShellParam(items);

        String pathName = findParam(params, flag);
        map.put(ServiceVOFieldConstant.field_path_name, flag + pathName);
        return map;
    }

    /**
     * 截取linux命令行分解的item中的参数部分
     * linux的ps -aux|grep 命令返回的内容，前面10项是linux系统的命令行信息，从11开始是用户自定义的用户参数
     * 所以，可以将第11个数据之后的项目取出来，就可以知道用户输入的参数
     *
     * @param items
     * @return 用户参数
     */
    private static List<String> splitLinuxShellParam(String[] items) {
        List<String> params = new ArrayList<>();
        for (int i = 11; i < items.length; i++) {
            params.add(items[i]);
        }

        return params;
    }

    /**
     * fox-edge的shell格式都是如下参数的
     * java -jar /opt/fox-edge/bin/xxx/xxxx 例如，/opt/fox-edge/bin/service/fox-edge-server-proxy-redis-topic-service-1.0.0.jar
     *
     * @param params
     * @param path
     * @return
     */
    private static Map<String, Object> splitFoxEdgeParam(List<String> params, String path) {
        Map<String, Object> result = new HashMap<>();
        if (params.size() < 3) {
            return result;
        }

        // 12 /opt/fox-edge/bin/xxx/xxxx 例如，/opt/fox-edge/bin/service/fox-edge-server-proxy-redis-topic-service-1.0.0.jar

        // 分解出路径名称和文件名称
        String appNameAndFileName = findParam(params, path);
        if (appNameAndFileName != null) {
            String pathName = path + appNameAndFileName;
            result.put(ServiceVOFieldConstant.field_path_name, pathName);

            String[] items = pathName.split("/");
            if (items.length > 0) {
                result.put(ServiceVOFieldConstant.field_file_name, items[items.length - 1]);
            }
        }


        String appName = findParam(params, "--app_name=");
        String springRedisHost = findParam(params, "--spring.redis.host=");
        String springRedisPort = findParam(params, "--spring.redis.port=");
        String serverPort = findParam(params, "--server.port=");
        if (serverPort == null || serverPort.equals("")) {
            // python/python3的命令行参数是server.port=，java版的命令行参数--server.port=
            serverPort = findParam(params, "server.port=");
        }


        if (appName != null && !appName.isEmpty()) {
            result.put(ServiceVOFieldConstant.field_app_name, appName);
        }
        if (springRedisHost != null && !springRedisHost.isEmpty()) {
            result.put("redisHost", springRedisHost);
        }
        if (springRedisPort != null && !springRedisPort.isEmpty()) {
            result.put("redisPort", springRedisPort);
        }
        if (serverPort != null && !serverPort.isEmpty()) {
            try {
                Integer appPort = Integer.parseInt(serverPort);
                result.put(ServiceVOFieldConstant.field_app_port, appPort);
            } catch (Exception e) {
            }
        }


        return result;
    }

    private static String findParam(List<String> shellParams, String springHeader) {
        for (String shellParam : shellParams) {
            if (!shellParam.startsWith(springHeader)) {
                continue;
            }

            return shellParam.substring(springHeader.length());
        }

        return null;
    }

    /**
     * GC java进程
     *
     * @param pid
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<String> gcProcess(Long pid) throws IOException, InterruptedException {
        return ShellUtils.executeShell("jmap -histo:live " + pid + " | head -10");
    }

    private static String[] filterDebug(String[] items) {
        List<String> list = new ArrayList<>();
        for (String item : items) {
            // 过滤掉idea的调试信息
            if (item.startsWith("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=")) {
                continue;
            }
            list.add(item);
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * 通过linux的netstat命令，根据端口号，查找进程ID
     * 在Linux下，如果该端口被使用，那么返回的是如下内容
     * tcp6       0      0 :::9000                 :::*                    LISTEN      2012423/java
     *
     * @param appPort 端口号
     * @return 进程的PID，如果没有被占用，那么返回的是null
     * @throws IOException          异常信息
     * @throws InterruptedException 异常信息
     */
    public static Long findPidByPort(Integer appPort) throws IOException, InterruptedException {
        List<String> shellLineList = ShellUtils.executeShell("netstat -anp | grep " + appPort);
        for (String shellLine : shellLineList) {
            String[] items = shellLine.split("\\s+");
            if (items.length != 7) {
                continue;
            }

            String ipAndPort = items[3];
            String pidAndCmd = items[6];

            String[] portItems = ipAndPort.split(":");

            // 检查：是否为指定的端口
            if (portItems.length > 0 && portItems[portItems.length - 1].equals(appPort.toString())) {
                String[] pidItems = pidAndCmd.split("/");
                if (pidItems.length > 0) {
                    try {
                        return Long.parseLong(pidItems[0]);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 获得当前进程的ID
     *
     * @return
     */
    public static final long getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Long.valueOf(runtimeMXBean.getName().split("@")[0]).longValue();
    }

    public static boolean killLoader() {
        try {
            // 检查：是否是LINUX操作系统，只有在该环境下，才会运行loader方式
            String OS = System.getProperty("os.name").toLowerCase();
            if (OS.indexOf("linux") < 0) {
                return false;
            }

            // 获得当前目录下的所有jar pid和loader pid
            Map<Long, Long> jar2loader = getLoaderPid();
            if (jar2loader == null || jar2loader.isEmpty()) {
                return false;
            }

            // 获得当前java进程的PID
            Long jarPid = getProcessID();

            Long loaderPid = jar2loader.get(jarPid);
            if (loaderPid == null) {
                return false;
            }

            // kill掉loader
            ShellUtils.executeShell("kill -9 " + loaderPid);

            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
