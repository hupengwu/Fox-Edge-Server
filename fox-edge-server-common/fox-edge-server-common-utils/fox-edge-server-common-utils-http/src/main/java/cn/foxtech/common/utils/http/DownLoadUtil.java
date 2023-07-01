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
     * @param urlStr 待下载的http文件的uri链接
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
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        fos.close();
        inputStream.close();
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    public static void main(String[] args) throws IOException {
        try {
            downLoadFromHttpUrl("http://120.79.69.201:9002/fox-edge-server/tools/virtualserialportdriver8.rar1", "test.rar", "d:/", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
