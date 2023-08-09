package cn.foxtech.common.utils.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileNameUtils {
    /**
     * 通过递归，获得目录下的全体文件名
     *
     * @param dir       目录名
     * @param depth     深度扫描：也就是是否遍历下一级目录
     * @param fullName  是否是完整路文件名称
     * @param fileNames 文件名列表
     */
    public static void findFileList(String dir, boolean depth, boolean fullName, List<String> fileNames) {
        FileNameUtils.findFileList(new File(dir), depth, fullName, fileNames);
    }

    public static List<String> findFileList(String dir, boolean depth, boolean fullName) {
        List<String> fileNames = new ArrayList<>();
        FileNameUtils.findFileList(new File(dir), depth, fullName, fileNames);
        return fileNames;
    }

    /**
     * 读取目录下的所有文件
     *
     * @param dir 目录
     * @param depth 深度扫描：也就是是否遍历下一级目录
     * @param fullName 是否完整名
     * @param fileNames 保存文件名的集合
     */
    public static void findFileList(File dir, boolean depth, boolean fullName, List<String> fileNames) {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < files.length; i++) {
            // 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            if (file.isFile()) {
                // 如果文件:添加文件全路径名
                if (!fullName) {
                    fileNames.add(file.getName());
                } else {
                    fileNames.add(file.getAbsolutePath());
                }

            } else if (depth) {
                // 如果是目录，并且需要深度搜索,回调自身继续查询
                findFileList(file, depth, fullName, fileNames);
            }
        }
    }
}
