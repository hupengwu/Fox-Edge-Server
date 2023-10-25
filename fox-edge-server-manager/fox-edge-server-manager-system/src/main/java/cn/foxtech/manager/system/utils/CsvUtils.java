package cn.foxtech.manager.system.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvUtils {
    /**
     * 读取
     *
     * @param path        csv文件路径
     * @param csvSplit    csv文件分隔符,默认“,”
     * @param charsetName csv文件编码格式，默认GBK
     * @return
     */
    public static List<Map<String, String>> readCsv(String path, String csvSplit, String charsetName) throws IOException {
        List<Map<String, String>> results = new ArrayList<>();
        BufferedReader bReader = null;
        File file = new File(path);
        if (charsetName == null || charsetName.isEmpty()) {
            charsetName = "UTF-8";
        }
        try {
            bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            String line = "";
            //忽略第一行标题
            List<String> titles = new ArrayList<>();
            String[] titleArr = bReader.readLine().split(csvSplit);
            for (String title : titleArr) {
                titles.add(title);
            }
            while ((line = bReader.readLine()) != null) {
                if (line.trim() != "") {
                    //分割开来的即是对应的每个单元格，注意空的情况
                    String[] valueArr = line.split(csvSplit);
                    int valueLength = valueArr.length;
                    Map<String, String> result = new HashMap<>();
                    for (int i = 0; i < titles.size(); i++) {
                        String title = titles.get(i);
                        String value = "";
                        if (i < valueLength) {
                            value = valueArr[i];
                        } else value = null;
                        result.put(title, value);
                    }
                    results.add(result);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (bReader != null) {
                bReader.close();
            }
        }
        return results;
    }

    /**
     * 写入CSV，csv文件分隔符,默认“,”
     *
     * @param filePath          csv文件路径
     * @param list              数据集
     * @param charsetName，默认GBK
     */
    public static void writeCSV(String filePath, List<String> list, String charsetName) {
        try {
            if (charsetName == null || charsetName.isEmpty()) {
                charsetName = "UTF-8";
            }

            // CSV文件需要的BOM
            byte[] bom ={(byte) 0xEF,(byte) 0xBB,(byte) 0xBF};
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(bom);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, charsetName);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            for (String s : list) {
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
