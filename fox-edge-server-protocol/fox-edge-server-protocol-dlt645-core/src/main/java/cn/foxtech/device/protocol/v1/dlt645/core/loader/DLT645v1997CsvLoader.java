package cn.foxtech.device.protocol.v1.dlt645.core.loader;

import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataEntity;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataFormatEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v1997DataEntity;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * HoldingRegisters数据实体的数据模板
 */
@Data
public class DLT645v1997CsvLoader {
    /**
     * 从CSV文件中装载映射表
     *
     * @param table csv表名称
     */
    public List<DLT645DataEntity> loadCsvFile(String table) {
        File dir = new File("");

        File file = new File(dir.getAbsolutePath() + "/template/" + table);
        CsvReader csvReader = CsvUtil.getReader();
        List<JDecoderValueParam> rows = csvReader.read(ResourceUtil.getReader(file.getPath(), CharsetUtil.CHARSET_GBK), JDecoderValueParam.class);

        // 将文件记录组织到map中
        List<DLT645DataEntity> list = new ArrayList<>();
        for (JDecoderValueParam jDecoderValueParam : rows) {
            try {
                DLT645v1997DataEntity entity = new DLT645v1997DataEntity();
                entity.setName(jDecoderValueParam.getName());
                entity.setDi1h((byte) Integer.parseInt(jDecoderValueParam.di1h, 16));
                entity.setDi1l((byte) Integer.parseInt(jDecoderValueParam.di1l, 16));
                entity.setDi0h((byte) Integer.parseInt(jDecoderValueParam.di0h, 16));
                entity.setDi0l((byte) Integer.parseInt(jDecoderValueParam.di0l, 16));
                entity.setLength(jDecoderValueParam.length);
                entity.setUnit(jDecoderValueParam.unit);
                entity.setRead(Boolean.parseBoolean(jDecoderValueParam.read));
                entity.setWrite(Boolean.parseBoolean(jDecoderValueParam.write));

                DLT645DataFormatEntity format = new DLT645DataFormatEntity();
                if (format.decodeFormat(jDecoderValueParam.format, jDecoderValueParam.length)) {
                    entity.setFormat(format);
                } else {
                    System.out.println("DLT645 CSV记录的格式错误:" + jDecoderValueParam.getName() + ":" + jDecoderValueParam.getFormat());
                    continue;
                }


                list.add(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }


    @Data
    static public class JDecoderValueParam implements Serializable {
        private String di1h;
        private String di1l;
        private String di0h;
        private String di0l;
        /**
         * 编码格式
         */
        private String format;
        /**
         * 长度
         */
        private Integer length;
        /**
         * 单位
         */
        private String unit;

        /**
         * 是否可读
         */
        private String read;
        /**
         * 是否可写
         */
        private String write;
        /**
         * 名称
         */
        private String name;
    }
}
