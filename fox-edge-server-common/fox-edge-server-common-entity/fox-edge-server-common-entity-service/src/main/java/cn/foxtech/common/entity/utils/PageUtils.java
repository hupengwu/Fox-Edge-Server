package cn.foxtech.common.entity.utils;

import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.mybatis.BaseEntityMapper;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;

import java.util.*;

public class PageUtils {
    public static Map<String, Object> getPageList(List entityList, int pageNum, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("total", entityList.size());


        List resultList = new ArrayList<>();

        int pageStartId = pageSize * (pageNum - 1);
        int pageEndId = pageSize * pageNum;
        int index = 0;
        for (Object entity : entityList) {
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


        return data;
    }

    /**
     * 获得分页列表数据
     *
     * @param entityList 实体列表
     * @param body       包含pageNum和pageSize参数的用户参数
     * @return 分页后的数据
     */
    public static AjaxResult getPageList(List<BaseEntity> entityList, Map<String, Object> body) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("total", entityList.size());


            // 根据ID排序
            Collections.sort(entityList, new Comparator<BaseEntity>() {
                public int compare(BaseEntity o1, BaseEntity o2) {
                    //降序
                    return o2.getId().compareTo(o1.getId());
                }
            });

            if (body.containsKey(BaseVOFieldConstant.field_page_num) && body.containsKey(BaseVOFieldConstant.field_page_size)) {
                List<BaseEntity> resultList = new ArrayList<>();

                int pageNum = Integer.parseInt(body.get(BaseVOFieldConstant.field_page_num).toString());
                int pageSize = Integer.parseInt(body.get(BaseVOFieldConstant.field_page_size).toString());
                int pageStartId = pageSize * (pageNum - 1);
                int pageEndId = pageSize * pageNum;
                int index = 0;
                for (BaseEntity entity : entityList) {
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


                data.put("list", EntityVOBuilder.buildVOList(resultList));
            } else {
                data.put("list", EntityVOBuilder.buildVOList(entityList));
            }

            return AjaxResult.success(data);

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获得分页列表数据
     *
     * @param entityList 实体列表
     * @param body       包含pageNum和pageSize参数的用户参数
     * @return 分页后的数据
     */
    public static AjaxResult getPageMapList(List<Map<String, Object>> entityList, Map<String, Object> body) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("total", entityList.size());

            if (body.containsKey(BaseVOFieldConstant.field_page_num) && body.containsKey(BaseVOFieldConstant.field_page_size)) {
                List<Map<String, Object>> resultList = new ArrayList<>();

                int pageNum = Integer.parseInt(body.get(BaseVOFieldConstant.field_page_num).toString());
                int pageSize = Integer.parseInt(body.get(BaseVOFieldConstant.field_page_size).toString());
                int pageStartId = pageSize * (pageNum - 1);
                int pageEndId = pageSize * pageNum;
                int index = 0;
                for (Map<String, Object> entity : entityList) {
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
                data.put("list", entityList);
            }

            return AjaxResult.success(data);

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 生成分页查询语句
     *
     * @param tableName 表名称
     * @param filter    过滤条件
     * @param order     ID按ASC/DESC排序
     * @param pageNmu   分页号
     * @param pageSize  分页大小
     * @return
     */
    public static String makeSelectSQLPage(String tableName, String filter, String order, Integer total, Long pageNmu, Long pageSize) {
        return makeSelectSQLPage(tableName, null, null, filter, order, total, pageNmu, pageSize);
    }

    /**
     * 生成分页查询语句
     *
     * @param tableName 表名称
     * @param vFields   字段列表
     * @param filter    过滤条件
     * @param order     ID按ASC/DESC排序
     * @param pageNmu   分页号
     * @param pageSize  分页大小
     * @return
     */
    public static String makeSelectSQLPage(String tableName, List<String> vFields, List<String> cFields, String filter, String order, Integer total, Long pageNmu, Long pageSize) {
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
     * @return
     */
    public static String makeSelectCountSQL(String tableName, String filter) {
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

    public static AjaxResult selectEntityListPage(BaseEntityMapper mapper, String tableName, String filter, String order, long pageNmu, long pageSize) {
        try {
            // 查询总数
            String selectCount = makeSelectCountSQL(tableName, filter);
            Integer total = mapper.executeSelectCount(selectCount);

            // 分页查询数据
            String selectPage = makeSelectSQLPage(tableName, filter, order, total, pageNmu, pageSize);
            List<BaseEntity> entityList = mapper.executeSelectData(selectPage);

            Map<String, Object> data = new HashMap<>();
            data.put("list", EntityVOBuilder.buildVOList(entityList));
            data.put("total", total);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    public static AjaxResult ajaxResultEmpty() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", new ArrayList<>());
        data.put("total", 0);

        return AjaxResult.success(data);
    }
}
