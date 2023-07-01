package cn.foxtech.common.utils.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * ini文件读取
 * 参考：https://www.jianshu.com/p/2e551ce239ab
 */
public class IniFileReader {

    protected HashMap sections = new HashMap();
    private transient String currentSecion;
    private transient Properties current;

    /**
     * 构造函数
     */
    public IniFileReader(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        read(reader);
        reader.close();
    }

    /**
     * 读取文件
     */
    protected void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line);
        }
    }

    /**
     * 解析配置文件行
     */
    @SuppressWarnings("unchecked")
    protected void parseLine(String line) {
        line = line.trim();
        if (line.matches("\\[.*]")) {
            //取出正则表达式匹配到的内容中的第一个条件组中的内容
            currentSecion = line.replaceFirst("\\[(.*)]", "$1");//[dev]
            //创建一个Properties对象
            current = new Properties();
            //将currentSecion和current以键值对的形式存放在map集合中。
            sections.put(currentSecion, current);
        } else if (line.matches(".*=.*")) {
            if (current != null) {

                int i = line.indexOf('=');
                String name = line.substring(0, i).trim();
                String value = line.substring(i + 1).trim();
                current.setProperty(name, value);
            }
        }
    }

    /**
     * 根据提供的键获取值
     */
    public String getValue(String section, String key) {
        Properties p = (Properties) sections.get(section);
        if (p == null) {
            return null;
        }
        String value = p.getProperty(key);
        return value;
    }
}
