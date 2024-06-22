package cn.foxtech.common.utils.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileCompareUtils {
    private static final byte[] data1 = new byte[1024];
    private static final byte[] data2 = new byte[1024];

    private static final int blockSize = 1024;

    /**
     * 通过二进制比较两个文件是否一样
     *
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
            if (len1 != len2) {
                return false;
            }

            // 比较大小相同的文件
            return compareFile(bis1, bis2, len2);
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

    private static synchronized boolean compareFile(BufferedInputStream bis1, BufferedInputStream bis2, int len) throws IOException {
        int number = len / blockSize;
        int remainder = len % blockSize;

        for (int i = 0; i < number; i++) {
            // 从文件读取一块数据
            bis1.read(data1);
            bis2.read(data2);

            // 比较数据
            if (compareBlock(blockSize)) {
                continue;
            }

            return false;
        }

        // 从文件读取一块数据
        bis1.read(data1);
        bis2.read(data2);

        boolean result = compareBlock(remainder);
        return result;
    }

    /**
     * 比较一块同等大小的内存数据
     *
     * @param size
     * @return
     */
    private static boolean compareBlock(int size) {
        for (int i = 0; i < size; i++) {
            if (data1[i] != data2[i]) {
                return false;
            }
        }

        return true;
    }
}
