package cn.foxtech.common.utils.shell;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 参考文章：<a href="https://blog.csdn.net/qq_37171353/article/details/109021361">Java中运行shell脚本</a>
 */
public class ShellUtils {
    /**
     * @param pathOrCommand 管道类型的命令，比如ps -aux|grep fox
     *                      windows平台使用 Runtime.getRuntime().exec(new String[]{"cmd", "/k", "cmd str"});
     *                      linux平台使用 Runtime.getRuntime().exec(new String[]{"sh", "-c", "cmd str"});
     * @return 返回的命令行
     */
    public static List<String> executeShell(String[] pathOrCommand) throws InterruptedException, IOException {
        List<String> result = new ArrayList<>();

        Process ps = null;
        BufferedInputStream in = null;
        BufferedReader br = null;

        try {
            // 执行脚本
            ps = Runtime.getRuntime().exec(pathOrCommand);
            int exitValue = ps.waitFor();
            if (0 != exitValue) {
                return result;
            }

            // 只能接收脚本echo打印的数据，并且是echo打印的最后一次数据
            in = new BufferedInputStream(ps.getInputStream());
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (br != null) {
                    br.close();
                }
                if (ps != null) {
                    ps.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return result;
    }

    /**
     * Linux版本执行管道命令
     *
     * @param pathOrCommand 命令行
     * @return 返回结果
     * @throws InterruptedException 异常
     * @throws IOException          异常
     */
    public static List<String> executeShell(String pathOrCommand) throws InterruptedException, IOException {
        return executeShell(new String[]{"sh", "-c", pathOrCommand});
    }

    /**
     * Windows版本执行管道命令
     *
     * @param pathOrCommand 命令行
     * @return 返回结果
     * @throws InterruptedException 异常
     * @throws IOException          异常
     */
    public static List<String> executeCmd(String pathOrCommand) throws InterruptedException, IOException {
        return executeShell(new String[]{"cmd", "/c", pathOrCommand});
    }
}
