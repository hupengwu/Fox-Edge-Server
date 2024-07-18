package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.shell.ShellUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 本地APP进程的启动与停止
 */
@Component
public class RepoLocalAppStartService {
    @Autowired
    private RepoLocalAppPortService appPortService;

    @Autowired
    private RepoLocalAppConfService appConfService;

    /**
     * 重启进程
     *
     * @param appName 应用名称
     * @param appType 应用类型
     * @throws IOException          异常信息
     * @throws InterruptedException 异常信息
     */
    public void restartProcess(String appName, String appType) throws IOException, InterruptedException {
        // 简单校验参数
        if (MethodUtils.hasEmpty(appName, appType)) {
            throw new ServiceException("参数不能为空:appName, appType");
        }

        Integer appPort = this.appPortService.getServicePort(appName, appType);

        // 重启设备服务进程
        File file = new File("");
        String shellName = file.getAbsolutePath() + "/shell/restart.sh " + appType + "/" + appName + " -p" + appPort;
        List<String> resultList = ShellUtils.executeShell(shellName);
        if (!resultList.isEmpty()) {
            throw new ServiceException(resultList.get(0));
        }
    }


    /**
     * 重启进程
     *
     * @param appName 应用名称
     * @param appType 应用类型
     * @throws IOException          异常信息
     * @throws InterruptedException 异常信息
     */
    public void stopProcess(String appName, String appType) throws IOException, InterruptedException {
        // 简单校验参数
        if (MethodUtils.hasEmpty(appName, appType)) {
            throw new ServiceException("参数不能为空:appName, appType");
        }

        List<Map<String, Object>> shellFileInfoList = this.appConfService.getConfFileInfoList();
        ProcessUtils.extendAppStatus(shellFileInfoList);

        for (Map<String, Object> map : shellFileInfoList) {
            if (!appName.equals(map.get(ServiceVOFieldConstant.field_app_name))) {
                continue;
            }
            if (!appType.equals(map.get(ServiceVOFieldConstant.field_app_type))) {
                continue;
            }
            if (appType.equals(map.get(ServiceVOFieldConstant.field_type_kernel))) {
                continue;
            }


            // 获得进程的PID，如果为空，则说明该进程并没有启动
            Object pid = map.get("pid");
            if (pid != null) {
                // kill掉该进程
                ShellUtils.executeShell("kill -9 " + pid);
            }
        }
    }
}
