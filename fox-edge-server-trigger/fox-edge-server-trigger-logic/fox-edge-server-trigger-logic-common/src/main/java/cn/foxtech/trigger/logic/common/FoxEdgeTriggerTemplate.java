package cn.foxtech.trigger.logic.common;

import cn.foxtech.common.utils.reflect.JarLoaderUtils;
import lombok.Data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FoxEdgeTriggerTemplate {
    private static final FoxEdgeTriggerTemplate template = new FoxEdgeTriggerTemplate();

    /**
     * modelName->methodName->FoxEdgeTrigger
     */
    private Map<String, Map<String, FoxEdgeTrigger>> triggers = new HashMap<>();

    public static FoxEdgeTriggerTemplate inst() {
        return template;
    }

    /**
     * 读取文本文件的所有行
     *
     * @param fileName 文件名
     * @return 所有行
     * @throws IOException
     */
    private static List<String> readTxtFileLines(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        List<String> lines = new ArrayList<>();
        String line = "";
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        isr.close();
        fis.close();

        return lines;
    }

    /**
     * 扫描触发器静态模块
     *
     * @param jarFileNameList
     */
    public void scanMethodPair(List<String> jarFileNameList) {
        try {
            // 动态装载配置文件中指明的解码器JAR包
            for (String line : jarFileNameList) {
                line = line.trim();
                JarLoaderUtils.loadJar(line);
            }

            // 然后通过扫描注解，生成操作定义表
            this.triggers = FoxEdgeTriggerScanner.scanTrigger();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    /**
     * 取得触发器
     *
     * @param modelName
     * @param methodName
     * @return
     */
    public FoxEdgeTrigger getEdgeTrigger(String modelName, String methodName) {
        Map<String, FoxEdgeTrigger> map = this.triggers.get(modelName);
        if (map == null) {
            return null;
        }

        return map.get(methodName);
    }

}
