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
     * @throws IOException 异常信息
     */
    public static void downLoadFromHttpUrl(String urlStr, String fileName, String savePath, String token) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.setRequestProperty("token", token);

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        ByteArrayOutputStream bos = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bos.toByteArray());
        fos.close();
        bos.close();
        inputStream.close();
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static ByteArrayOutputStream readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos;
    }

    public static void main(String[] args) throws IOException {
        try {
            downLoadFromHttpUrl("http://120.79.69.201:9002/fox-edge-server/tools/virtualserialportdriver8.rar1", "test.rar", "d:/", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试HTTP的文件是否能够打开：在下载前，可以提前测试一下，URL是否能够打开，来判定它是否有效
     * @param urlStr url链接
     * @param token token
     * @return 是否能够打开
     */
    public static boolean testUrlFileCanBeOpen(String urlStr, String token) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("token", token);

            // 如果文件不存在，此时打开流会异常
            InputStream inputStream = conn.getInputStream();

            inputStream.close();
        } catch (IOException e) {
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return true;
    }
}
