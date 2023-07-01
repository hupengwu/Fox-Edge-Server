package cn.foxtech.common.utils.file;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本文件工具
 */
public class FileTextUtils {
    /**
     * 分行读取文本数据
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<String> readTextFileLines(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        isr.close();
        fis.close();

        return lines;
    }

    public static String readTextFile(String fileName) throws IOException {
        return readTextFile(fileName, StandardCharsets.UTF_8);
    }

    public static String readTextFile(String fileName, Charset cs) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        return readTextFile(fis, cs);
    }

    public static String readTextFile(File file) throws IOException {
        return readTextFile(file, StandardCharsets.UTF_8);
    }

    public static String readTextFile(File file, Charset cs) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return readTextFile(fis, cs);
    }

    public static String readTextFile(InputStream fis, Charset cs) throws IOException {
        InputStreamReader isr = new InputStreamReader(fis, cs);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        isr.close();
        fis.close();

        return sb.toString();
    }

    /**
     * 用 BufferedWriter 写文件
     *
     * @param fileName 文件目录
     * @param content  待写入内容
     * @throws IOException
     */
    public static void writeTextFile(String fileName, String content) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            bufferedWriter.write(content);
        }
    }

}
