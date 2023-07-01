package cn.foxtech.common.utils.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileCompareUtils {
    /**
     * 通过二进制比较两个文件是否一样
     * @param filePath1 文件1
     * @param filePath2 文件2
     * @return 是否一样
     */
    public static boolean isSameFile(String filePath1, String filePath2) {
        BufferedInputStream bis1 = null;
        BufferedInputStream bis2 = null;
        FileInputStream fis1 = null;
        FileInputStream fis2 = null;

        try {
            // 获取文件输入流
            fis1 = new FileInputStream(filePath1);
            fis2 = new FileInputStream(filePath2);
            // 将文件输入流包装成缓冲流
            bis1 = new BufferedInputStream(fis1);
            bis2 = new BufferedInputStream(fis2);

            // 获取文件字节总数
            int len1 = bis1.available();
            int len2 = bis2.available();

            // 判断两个文件的字节长度是否一样,长度相同则比较具体内容
            if (len1 == len2) {
                // 建立字节缓冲区
                byte[] data1 = new byte[len1];
                byte[] data2 = new byte[len2];

                // 将文件写入缓冲区
                bis1.read(data1);
                bis2.read(data2);
                // 依次比较文件中的每个字节
                for (int i = 0; i < len1; i++) {
                    if (data1[i] != data2[i]) {
                        //System.out.println("文件内容不一致");
                        return false;
                    }
                }

                //System.out.println("文件内容一致");
                return true;
            } else {
                //System.out.println("文件长度不一致，内容不一致");
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis1 != null) {
                try {
                    bis1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis2 != null) {
                try {
                    bis2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis1 != null) {
                try {
                    fis1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis2 != null) {
                try {
                    fis2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

}
