/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dlt645.core.loader;

import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v2007DataEntity;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataFormatEntity;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * HoldingRegisters数据实体的数据模板
 */
@Data
public class DLT645v2007CsvLoader {
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
                DLT645v2007DataEntity entity = new DLT645v2007DataEntity();
                entity.setName(jDecoderValueParam.getName());
                entity.setDi0((byte) Integer.parseInt(jDecoderValueParam.di0, 16));
                entity.setDi1((byte) Integer.parseInt(jDecoderValueParam.di1, 16));
                entity.setDi2((byte) Integer.parseInt(jDecoderValueParam.di2, 16));
                entity.setDi3((byte) Integer.parseInt(jDecoderValueParam.di3, 16));

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
        private String di0;
        private String di1;
        private String di2;
        private String di3;
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
