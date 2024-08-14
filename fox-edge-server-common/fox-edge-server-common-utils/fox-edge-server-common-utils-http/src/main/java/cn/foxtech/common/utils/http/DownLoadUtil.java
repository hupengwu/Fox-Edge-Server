/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.http;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP文件下载工具
 */
public class DownLoadUtil {
    /**
     * 从网络Url中下载文件
     *
     * @param urlStr   待下载的http文件的uri链接
     * @param fileName 保存到本地的文件名称
     * @param savePath 保存的本地目录
     */
    public static void downLoadFromHttpUrl(String urlStr, String fileName, String savePath, String token) {
        HttpURLConnection conn = null;
        InputStream his = null;
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("token", token);

            //得到输入流
            his = conn.getInputStream();
            //获取自己数组
            bos = readInputStream(his);

            //文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            File file = new File(saveDir + File.separator + fileName);
            fos = new FileOutputStream(file);
            fos.write(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (bos != null) {
                    bos.reset();
                    bos.close();
                }
                if (his != null) {
                    his.reset();
                    his.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    private static ByteArrayOutputStream readInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bos = null;
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos;
        } catch (Exception ie) {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            throw ie;
        }

    }

    /**
     * 测试HTTP的文件是否能够打开：在下载前，可以提前测试一下，URL是否能够打开，来判定它是否有效
     *
     * @param urlStr url链接
     * @param token  token
     * @return 是否能够打开
     */
    public static boolean testUrlFileCanBeOpen(String urlStr, String token) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("token", token);

            // 如果文件不存在，此时打开流会异常
            is = conn.getInputStream();
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }


}
