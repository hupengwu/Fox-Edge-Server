/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicesequence;


import cn.foxtech.common.entity.constant.Constants;
import cn.foxtech.common.entity.entity.DeviceSequenceEntity;
import cn.foxtech.common.entity.entity.DeviceSequencePo;
import cn.foxtech.common.entity.service.tablename.TableNameService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DeviceSequenceEntityService {
    @Autowired(required = false)
    private DeviceSequenceEntityMapper mapper;

    @Autowired(required = false)
    private TableNameService tableNameService;

    public String getEntityType() {
        return Constants.DeviceSequenceEntity;
    }

    /**
     * 查询记录
     *
     * @param body
     * @return
     */
    public List<DeviceSequenceEntity> queryRecords(Map<String, Object> body) {
        // 取出表名称上的序号
        List<Long> sequence = this.tableNameService.queryTableSequence("tb_sequence_");

        // 检查：是否需要只处理记录时间内的数据
        if (body.containsKey("recordTime")) {
            Long recordTime = NumberUtils.makeLong(body.getOrDefault("recordTime", 0));
            sequence = limitByRecordTime(sequence, recordTime);
        }

        // 到各个分表上取数据
        return queryRecords(sequence, body);
    }

    /**
     * 逐个表的查询记录
     *
     * @param sequence
     * @param body
     * @return
     */
    private List<DeviceSequenceEntity> queryRecords(List<Long> sequence, Map<String, Object> body) {
        Integer pageSize = NumberUtils.makeInteger(body.getOrDefault("pageSize", 10));

        List<DeviceSequenceEntity> entityList = new ArrayList<>();
        for (Long time : sequence) {
            String tableName = "tb_sequence_" + String.format("%010d", time);

            List<DeviceSequenceEntity> list = DeviceSequenceEntityMaker.makePoList2EntityList(this.queryRecords(tableName, body));
            if (entityList.size() + list.size() < pageSize) {
                entityList.addAll(list);
                continue;
            } else if (entityList.size() + list.size() == pageSize) {
                entityList.addAll(list);
                break;
            } else {
                entityList.addAll(list.subList(0, pageSize - entityList.size()));
                break;
            }
        }

        return entityList;
    }

    /**
     * 查询单个表的记录
     *
     * @param tableName
     * @param body
     * @return
     */
    private List<DeviceSequencePo> queryRecords(String tableName, Map<String, Object> body) {
        Long recordTime = NumberUtils.makeLong(body.getOrDefault("recordTime", 0));
        Integer pageSize = NumberUtils.makeInteger(body.getOrDefault("pageSize", 10));
        String deviceName = (String) body.getOrDefault("deviceName", "");
        String metricName = (String) body.getOrDefault("metricName", "");

        if (deviceName.isEmpty() && metricName.isEmpty()) {
            return this.mapper.select(tableName, recordTime, pageSize);
        }
        if (!deviceName.isEmpty() && metricName.isEmpty()) {
            return this.mapper.selectByDeviceName(tableName, deviceName, recordTime, pageSize);
        }
        if (deviceName.isEmpty() && !metricName.isEmpty()) {
            return this.mapper.selectByMetricName(tableName, metricName, recordTime, pageSize);
        }
        if (!deviceName.isEmpty() && !metricName.isEmpty()) {
            return this.mapper.selectByDeviceNameAndMetricName(tableName, deviceName, metricName, recordTime, pageSize);
        }

        throw new RuntimeException("不支持的场景");
    }


    private List<Long> limitByRecordTime(List<Long> times, Long recordTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // 设置时区，例如北京时间
        recordTime = NumberUtils.parseLong(sdf.format(new Date(recordTime)));

        List<Long> result = new ArrayList<>();
        for (Long time : times) {
            if (recordTime <= time) {
                result.add(time);
            }
        }

        return result;
    }

    public void insertEntity(DeviceSequenceEntity entity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tableName = "tb_sequence_" + sdf.format(new Date());

        String deviceValue = JsonUtils.buildJsonWithoutException(entity.getDeviceValue());

        try {
            this.mapper.insertRecord(tableName, entity.getDeviceName(), entity.getMetricName(), entity.getTimestamp(), deviceValue, System.currentTimeMillis());
        } catch (BadSqlGrammarException bse) {
            // 检查：是否是因为数据库表不存在导致的异常
            SQLException se = bse.getSQLException();
            if (se != null && se.getErrorCode() == 1146) {
                try {
                    // 创建数据库表
                    String sql = "CREATE TABLE tb_sequence_2025091110 (\n" + "\tid BIGINT NOT NULL AUTO_INCREMENT COMMENT '序号',\n" + "\tdevice_name VARCHAR(50) NULL DEFAULT NULL COMMENT '设备名称' COLLATE 'utf8mb4_0900_ai_ci',\n" + "\tmetric_name VARCHAR(50) NULL DEFAULT NULL COMMENT '指标名称' COLLATE 'utf8mb4_0900_ai_ci',\n" + "\ttimestamp BIGINT NULL DEFAULT NULL COMMENT '时间戳',\n" + "\tdevice_value JSON NULL DEFAULT NULL COMMENT '设备数值',\n" + "\tcreate_time BIGINT NULL DEFAULT NULL COMMENT '创建时间',\n" + "\tPRIMARY KEY (id) USING BTREE,\n" + "\tUNIQUE INDEX device_name_metric_name_timestamp (device_name, metric_name, timestamp) USING BTREE,\n" + "\tINDEX device_name (device_name) USING BTREE,\n" + "\tINDEX metric_name (metric_name) USING BTREE,\n" + "\tINDEX timestamp (timestamp) USING BTREE\n" + ")\n" + "COLLATE='utf8mb4_0900_ai_ci'\n" + "ENGINE=InnoDB\n" + "AUTO_INCREMENT=0\n" + ";\n";
                    sql = sql.replaceAll("tb_sequence_2025091110", tableName);
                    this.mapper.executeInsert(sql);

                    // 重新插入数据
                    this.mapper.insertRecord(tableName, entity.getDeviceName(), entity.getMetricName(), entity.getTimestamp(), deviceValue, System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    public void deleteRecords(int maxHour) {
        LocalDateTime time = LocalDateTime.now().minusHours(maxHour);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        Long timestamp = NumberUtils.parseLong(time.format(formatter));

        // 取出表名称上的序号
        List<Long> sequenceList = this.tableNameService.queryTableSequence("tb_sequence_");
        for (Long sequence : sequenceList) {
            if (timestamp > sequence) {
                String tableName = "tb_sequence_" + String.format("%010d", sequence);
                this.mapper.dropTable(tableName);
            }
        }
    }

}
