package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.utils.osinfo.OSInfo;
import cn.foxtech.common.utils.shell.ShellUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class RepoLocalShellService {
    @Autowired
    private RepoLocalPathNameService pathNameService;

    public void packCsvTemplate2TarFile11(String modelType, String filePath, String tarFileName, String jarFileName) throws IOException, InterruptedException {
        if (OSInfo.isWindows()) {
            filePath = filePath.replace("/", "\\");

            // 打包成tar文件
            ShellUtils.executeCmd("tar -cvf " + filePath + "\\" + tarFileName + " -C " + filePath + " " + jarFileName);

            // 删除临时的jar文件
            ShellUtils.executeCmd("del /q " + filePath + "\\" + jarFileName);
            return;
        }
        if (OSInfo.isLinux()) {
            filePath = filePath.replace("\\", "/");

            // 打包成tar文件
            ShellUtils.executeShell("tar -cvf " + filePath + "/" + tarFileName + " -C " + filePath + " " + jarFileName);

            // 删除临时的jar文件
            ShellUtils.executeShell("rm -f " + filePath + "/" + jarFileName);
            return;
        }


        return;
    }

    /**
     * 打包文件 \opt\fox-edge\template\dobot-mg400\v1\1.0.0\*.*
     */
    public List<String> packCsvTemplate2TarFile(String tarFileName, String modelName) throws IOException, InterruptedException {
        String pathName = this.pathNameService.getPathName4LocalTemplate2version(modelName);
        File dir = new File(pathName);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new ServiceException("目录不存在！");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("tar -cvf \"" + pathName + "/" + tarFileName + "\" -C " + pathName);
        for (String fileName : dir.list()) {
            if (fileName.endsWith(".tar")) {
                continue;
            }

            sb.append(" \"" + fileName + "\"");
        }

        return this.executeShell(sb.toString());
    }

    public List<String> executeShell(String cmd) throws IOException, InterruptedException {
        if (OSInfo.isWindows()) {
            String command = cmd.replace("/", "\\");
            return ShellUtils.executeCmd(command);
        }
        if (OSInfo.isLinux()) {
            String command = cmd.replace("\\", "/");
            return ShellUtils.executeShell(command);
        }

        throw new ServiceException("不支持的操作系统类型");
    }
}
