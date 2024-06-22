package cn.foxtech.common.utils.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 导出HTTP下载文件
 */
public class ExportUtil {
    public static void exportTextFile(HttpServletResponse response, String path, String fileName) {
        File download = new File(path + "/" + fileName);
        if (download.exists()) {
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));

            InputStream is = null;
            ServletOutputStream os = null;
            try {
                is = new FileInputStream(download);
                os = response.getOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = is.read(b)) != -1) {
                    os.write(b, 0, n);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
