/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.foxsql;


import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FoxSqlService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private FoxSqlMapper foxSqlMapper;

    /**
     * 扁平化MapList
     *
     * @param mapList map列表
     * @return map列表
     */
    public List<Map<String, Object>> flatMapList(Collection<Map<String, Object>> mapList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Map<String, Object> result = new HashMap<>();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                result.put(key, value);

                if (value == null) {
                    continue;
                }
                if (value instanceof Map) {
                    Map<String, Object> smap = (Map<String, Object>) value;
                    for (String skey : smap.keySet()) {
                        result.put(skey, smap.get(skey));
                    }
                }
            }

            resultList.add(result);
        }

        return resultList;
    }

    /**
     * 将json字段转换陈map对象字段
     *
     * @param mapList map列表
     * @param jsnKey jsonKey
     * @param objKey objectKey
     */
    public void buildJsonField(Collection<Map<String, Object>> mapList, String jsnKey, String objKey) {
        for (Map<String, Object> map : mapList) {
            String jsn = (String) map.get(jsnKey);
            if (jsn == null || jsn.isEmpty()) {
                continue;
            }

            map.put(objKey, JsonUtils.buildMapWithDefault(jsn, new HashMap<>()));
        }
    }

    public void buildJsonField(Map<String, Object> map, String jsnKey, String objKey) {
        String jsn = (String) map.get(jsnKey);
        if (jsn == null || jsn.isEmpty()) {
            return;
        }

        map.put(objKey, JsonUtils.buildMapWithDefault(jsn, new HashMap<>()));
    }

    /**
     * 剔除某些字段
     *
     * @param mapList map列表
     * @param filterKey 过滤key
     */
    public void filterKey(Collection<Map<String, Object>> mapList, String filterKey) {
        List<String> filterKeys = new ArrayList<>();
        filterKeys.add(filterKey);
        filterKeys(mapList, filterKeys);
    }

    /**
     * 过滤掉某些字段
     *
     * @param mapList map列表
     * @param filterKeys 过滤key
     */
    public void filterKeys(Collection<Map<String, Object>> mapList, List<String> filterKeys) {
        for (Map<String, Object> map : mapList) {
            for (String filter : filterKeys) {
                map.remove(filter);
            }
        }
    }

    /**
     * 转换为驼峰
     * @param mapList 对象列表
     */
    public void toCamelCase(List<Map<String, Object>> mapList) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            Map<String, Object> result = new HashMap<>();
            for (String key : map.keySet()) {
                result.put(StringUtils.camelName(key), map.get(key));
            }

            resultList.add(result);
        }


        mapList.clear();
        mapList.addAll(resultList);
    }


    /**
     * 生成分页查询语句
     *
     * @param tableName 表名称
     * @param filter    过滤条件
     * @param order     ID按ASC/DESC排序
     * @param total     总数
     * @param pageNmu   分页号
     * @param pageSize  分页大小
     * @return sql
     */
    public String makeSelectSQLPage(String tableName, String filter, String order, Integer total, Long pageNmu, Long pageSize) {
        return makeSelectSQLPage(tableName, null, null, filter, order, total, pageNmu, pageSize);
    }

    /**
     * 生成分页查询语句
     *
     * @param tableName 表名称
     * @param vFields   字段列表
     * @param cFields   组合字段
     * @param filter    过滤条件
     * @param order     ID按ASC/DESC排序
     * @param total     总数
     * @param pageNmu   分页号
     * @param pageSize  分页大小
     * @return sql
     */
    public String makeSelectSQLPage(String tableName, List<String> vFields, List<String> cFields, String filter, String order, Integer total, Long pageNmu, Long pageSize) {
        StringBuilder sb = new StringBuilder();

        Long pagePos = pageNmu * pageSize;
        Long pageCount = (total / pageSize) >= pageNmu ? pageSize : total % pageSize;


        String where = "";
        if (filter != null && !filter.isEmpty()) {
            where = "WHERE (" + filter + ")";
        }
        String ASC = "ASC";
        String DESC = "DESC";
        if (order.equals("DESC".toUpperCase())) {
            ASC = "DESC";
            DESC = "ASC";
        }

        StringBuilder sbf = new StringBuilder();
        if (MethodUtils.hasEmpty(vFields, cFields)) {
            sbf.append("t4.* ,");
        }
        if (!MethodUtils.hasEmpty(vFields)) {
            for (String field : vFields) {
                sbf.append("t4.");
                sbf.append(field);
                sbf.append(",");
            }
        }
        if (!MethodUtils.hasEmpty(cFields)) {
            for (String field : cFields) {
                sbf.append(field);
                sbf.append(",");
            }
        }

        String fs = sbf.toString();
        fs = fs.substring(0, fs.length() - 1);

        sb.append("        SELECT " + fs + "                        ");
        sb.append("        FROM " + tableName + " t4                ");
        sb.append("        RIGHT JOIN                               ");
        sb.append("        (                                        ");
        sb.append("                SELECT t2.id                     ");
        sb.append("                FROM                             ");
        sb.append("        (                                        ");
        sb.append("                SELECT t1.id                     ");
        sb.append("        FROM " + tableName + " t1                ");
        sb.append(where);
        sb.append("        ORDER BY t1.id " + ASC + "               ");
        sb.append("        LIMIT " + pagePos + "                    ");
        sb.append("                     ) t2                        ");
        sb.append("        ORDER BY t2.id " + DESC + "              ");
        sb.append("        LIMIT " + pageCount + "                  ");
        sb.append("             ) t3                                ");
        sb.append("        ON t4.id = t3.id                         ");
        sb.append("        ORDER BY t4.id " + ASC + "               ");


        return sb.toString();
    }

    /**
     * 生成查询数量语句
     *
     * @param tableName 表名称
     * @param filter    过滤条件
     * @return sql
     */
    public String makeSelectCountSQL(String tableName, String filter) {
        StringBuilder sb = new StringBuilder();

        String where = "";
        if (filter != null && !filter.isEmpty()) {
            where = "WHERE (" + filter + ")";
        }

        sb.append("        SELECT count(1)                          ");
        sb.append("        FROM " + tableName + " t1                ");
        sb.append(where);

        return sb.toString();
    }

    public Map<String, Object> resultEmpty() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", new ArrayList<>());
        data.put("total", 0);

        return data;
    }

    /**
     * 将数字类型的对象，转换为Long类型
     *
     * @param object 对象
     * @return Long数值
     */
    public Long makeLong(Object object) {
        if (object instanceof Long) {
            return (Long) object;
        }
        if (object instanceof Integer) {
            return ((Integer) object).longValue();
        }
        if (object instanceof Short) {
            return ((Short) object).longValue();
        }

        return null;
    }

    public Map<String, Object> selectMapListByPage(String tableName, String filter, String order, long pageNmu, long pageSize) {
        return this.selectMapListByPage(tableName, filter, order, pageNmu, pageSize, true, new HashMap<>());
    }

    /**
     * 对数据库进行分页查询
     *
     * @param tableName 数据库表或者视图的名称
     * @param filter    过滤条件，也就是where后面的语句
     * @param order     按ID进行排序方式 ASC/DESC
     * @param pageNmu   第几页
     * @param pageSize  每页大小
     * @return 查询结果
     */
    public Map<String, Object> selectMapListByPage(String tableName, String filter, String order, long pageNmu, long pageSize, boolean toCamelCase, Map<String, String> jsn2obj) {
        // 查询总数
        String selectCount = this.makeSelectCountSQL(tableName, filter);
        Integer total = this.foxSqlMapper.selectCount(selectCount);

        // 分页查询数据
        String selectPage = this.makeSelectSQLPage(tableName, filter, order, total, pageNmu, pageSize);
        List<Map<String, Object>> mapList = this.foxSqlMapper.selectMapList(selectPage);

        // 驼峰转换
        if (toCamelCase) {
            this.toCamelCase(mapList);
        }

        for (String key : jsn2obj.keySet()) {
            // 将dataJson文本转换为map对象
            this.buildJsonField(mapList, key, jsn2obj.get(key));
            // 剔除不能输出的字段
            this.filterKey(mapList, key);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", mapList);
        data.put("total", total);

        return data;
    }

    /**
     * 查询下数据库表的json列
     * 背景：json列的数据直接返回的是文本，它需要转换为HashMap对象进行处理
     *
     * @param tableName 表名称
     * @return 返回值
     */
    public List<String> selectJsonColumns(String tableName) {
        List<String> result = new ArrayList<>();

        List<Map<String, Object>> mapList = this.selectMapList("SHOW  COLUMNS  FROM  " + tableName, false);
        for (Map<String, Object> map : mapList) {
            String type = (String) map.get("Type");
            String field = (String) map.get("Field");
            if ("json".equals(type)) {
                result.add(field);
            }
        }

        return result;
    }

    public List<Map<String, Object>> selectColumns(String tableName) {
        return this.selectMapList("SHOW  COLUMNS  FROM  " + tableName, false);
    }

    public List<Map<String, Object>> selectMapList(String selectSql) {
        return this.selectMapList(selectSql, true);
    }

    public List<Map<String, Object>> selectMapList(String selectSql, boolean toCamelCase) {
        return this.selectMapList(selectSql, toCamelCase, new HashSet<>());
    }

    public List<Map<String, Object>> selectMapList(String selectSql, boolean toCamelCase, Set<String> jsn2obj) {
        List<Map<String, Object>> mapList = this.foxSqlMapper.selectMapList(selectSql);

        for (String key : jsn2obj) {
            // 将dataJson文本转换为map对象
            this.buildJsonField(mapList, key, key);
        }

        // 驼峰转换
        if (toCamelCase) {
            this.toCamelCase(mapList);
        }

        return mapList;
    }

    /**
     * 对数据列表进行分页处理
     *
     * @param mapList       实体列表
     * @param fieldPrimeKey 主字段
     * @param fieldPageNum 页码
     * @param fieldPageSize 页大小
     * @param body 参数
     * @return 查询结果
     */
    public Map<String, Object> selectMapListByPage(List<Map<String, Object>> mapList, String fieldPrimeKey, String fieldPageNum, String fieldPageSize, Map<String, Object> body) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", mapList.size());

        // 根据ID排序
        Collections.sort(mapList, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                //降序
                Long v2 = makeLong(o2.getOrDefault(fieldPrimeKey, 0L));
                Long v1 = makeLong(o1.getOrDefault(fieldPrimeKey, 0L));
                return v2.compareTo(v1);
            }
        });

        if (body.containsKey(fieldPageNum) && body.containsKey(fieldPageSize)) {
            List<Map<String, Object>> resultList = new ArrayList<>();

            int pageNum = Integer.parseInt(body.get(fieldPageNum).toString());
            int pageSize = Integer.parseInt(body.get(fieldPageSize).toString());
            int pageStartId = pageSize * (pageNum - 1);
            int pageEndId = pageSize * pageNum;
            int index = 0;
            for (Map<String, Object> entity : mapList) {
                if (index < pageStartId) {
                    index++;
                    continue;
                }
                if (index >= pageEndId) {
                    break;
                }

                resultList.add(entity);
                index++;
            }


            data.put("list", resultList);
        } else {
            data.put("list", mapList);
        }

        return data;
    }

    /**
     * 从用户输入的body中，提取需要的字段
     *
     * @param body body参数
     * @param paramList 参数列表
     * @return 转换结果
     */
    public Map<String, Object> makeRecord(Map<String, Object> body, String... paramList) {
        String[] var1 = paramList;
        int var2 = paramList.length;

        Map<String, Object> result = new HashMap<>();
        for (int var3 = 0; var3 < var2; ++var3) {
            String param = var1[var3];
            if (body.containsKey(param)) {
                result.put(param, body.get(param));
            } else {
                throw new RuntimeException("缺少参数:" + param);
            }
        }

        return result;
    }

    public List<Map<String, Object>> selectOptionList(String tableName, String underField1, boolean toCamelCase) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ").append(underField1).append(" FROM ").append(tableName);
        return this.selectMapList(sb.toString(), toCamelCase, new HashSet<>());
    }

    public List<Map<String, Object>> selectOptionList(String tableName, String underField1, String underField2, Object value1, boolean toCamelCase) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ").append(underField1).append(", ").append(underField2).append(" FROM ").append(tableName).append(" WHERE ");
        if (value1 == null) {
            sb.append(underField1).append(" IS NULL");
        } else if (value1 instanceof String) {
            sb.append(underField1).append(" = '").append(value1).append("'");
        } else {
            sb.append(underField1).append(" = ").append(value1);
        }

        return this.selectMapList(sb.toString(), toCamelCase, new HashSet<>());
    }

    public List<Map<String, Object>> selectOptionList(String tableName, String underField1, String underField2, boolean toCamelCase) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ").append(underField1).append(", ").append(underField2).append(" FROM ").append(tableName);
        return this.selectMapList(sb.toString(), toCamelCase, new HashSet<>());
    }

    public List<Map<String, Object>> selectOptionList(String tableName, String underField1, String underField2, String underField3, Object value1, boolean toCamelCase) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ").append(underField1).append(", ").append(underField2).append(", ").append(underField3).append(" FROM ").append(tableName).append(" WHERE ");
        if (value1 == null) {
            sb.append(underField1).append(" IS NULL");
        } else if (value1 instanceof String) {
            sb.append(underField1).append(" = '").append(value1).append("'");
        } else {
            sb.append(underField1).append(" = ").append(value1);
        }

        return this.selectMapList(sb.toString(), toCamelCase, new HashSet<>());
    }

    // "SELECT DISTINCT " + underField1 + ", " + underField2 + " FROM " + tableName + " WHERE " + underField1 + "=" + value1
}
