/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.iotdb;

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.rpc.TSStatusCode;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IoTDBSessionPool {
    private SessionPool sessionPool;


    public void initialize(String host, int port, String username, String password) {
        this.sessionPool = new SessionPool(host, port, username, password, 4);
    }

    public boolean isInitialize() {
        return this.sessionPool != null;
    }

    public void createDatabase(String database) throws IoTDBConnectionException, StatementExecutionException {
        try {
            this.sessionPool.createDatabase(database);
        } catch (StatementExecutionException e) {
            if (e.getStatusCode() != TSStatusCode.DATABASE_ALREADY_EXISTS.getStatusCode()) {
                throw e;
            }
        }
    }

    public void insertRecord(String deviceId, long time, Map<String, Object> data) throws IoTDBConnectionException, StatementExecutionException {
        if (data == null || data.isEmpty()) {
            return;
        }

        List<String> measurements = new ArrayList<>();
        List<TSDataType> types = new ArrayList<>();
        List<Object> values = new ArrayList<>();


        for (String key : data.keySet()) {
            if (key.contains(".")) {
                continue;
            }

            Object value = data.get(key);

            if (value == null) {
                types.add(TSDataType.INT64);
            } else if (value instanceof Integer) {
                types.add(TSDataType.INT32);
            } else if (value instanceof Long) {
                types.add(TSDataType.INT64);
            } else if (value instanceof Float) {
                types.add(TSDataType.FLOAT);
            } else if (value instanceof Double) {
                types.add(TSDataType.DOUBLE);
            } else if (value instanceof Boolean) {
                types.add(TSDataType.BOOLEAN);
            } else {
                continue;
            }

            measurements.add(key);
            values.add(value);
        }

        if (values.isEmpty()) {
            return;
        }

        this.sessionPool.insertRecord(deviceId, time, measurements, types, values);
    }


    public SessionDataSetWrapper executeQuery(String deviceId, List<String> keys) throws IoTDBConnectionException, StatementExecutionException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for (String key : keys) {
            sb.append(key + ", ");
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append("FROM ");
        sb.append(deviceId);

        return this.executeQueryStatement(sb.toString());
    }

    public List<List<Object>> readRecord(SessionDataSetWrapper dataSet) throws IoTDBConnectionException, StatementExecutionException {
        List<List<Object>> result = new ArrayList<>();

        while (dataSet.hasNext()) {
            RowRecord record = dataSet.next();

            List<Object> row = new ArrayList<>();
            row.add(record.getTimestamp());

            for (Field field : record.getFields()) {
                TSDataType dataType = field.getDataType();

                Object value = 0;
                if (dataType.equals(TSDataType.INT32)) {
                    value = field.getIntV();
                } else if (dataType.equals(TSDataType.INT64)) {
                    value = field.getLongV();
                } else if (dataType.equals(TSDataType.DOUBLE)) {
                    value = field.getDoubleV();
                } else if (dataType.equals(TSDataType.FLOAT)) {
                    value = field.getFloatV();
                } else if (dataType.equals(TSDataType.BOOLEAN)) {
                    value = field.getBoolV();
                } else {
                    value = null;
                }
                row.add(value);
            }

            result.add(row);
        }

        return result;
    }

    public SessionDataSetWrapper executeQueryStatement(String sql) throws IoTDBConnectionException, StatementExecutionException {
        if (this.sessionPool == null) {
            throw new IoTDBConnectionException("会话已断开!");
        }

        SessionDataSetWrapper dataSet = this.sessionPool.executeQueryStatement(sql);
        return dataSet;
    }

    public SessionDataSetWrapper lastDataQuery(String deviceId, List<String> keys, long lastTime) throws IoTDBConnectionException, StatementExecutionException {
        if (this.sessionPool == null) {
            throw new IoTDBConnectionException("会话已断开!");
        }

        List<String> paths = new ArrayList<>();
        for (String key : keys) {
            paths.add(deviceId + "." + key);
        }

        return this.sessionPool.executeLastDataQuery(paths, lastTime, 60000);
    }

    public void deleteData(String deviceId, long endTime) throws IoTDBConnectionException, StatementExecutionException {
        if (this.sessionPool == null) {
            throw new IoTDBConnectionException("会话已断开!");
        }

        this.sessionPool.deleteData(deviceId + ".*", endTime);
    }

    public void deleteData(String deviceId, List<String> keys, long endTime) throws IoTDBConnectionException, StatementExecutionException {
        if (this.sessionPool == null) {
            throw new IoTDBConnectionException("会话已断开!");
        }

        List<String> paths = new ArrayList<>();
        for (String key : keys) {
            paths.add(deviceId + "." + key);
        }

        this.sessionPool.deleteData(paths, endTime);
    }
}
