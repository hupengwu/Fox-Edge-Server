package cn.foxtech.manager.system.controller;

import cn.foxtech.common.entity.manager.EntityOptionManager;
import cn.foxtech.common.entity.service.foxsql.FoxSqlService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/option")
public class OptionManageController {


    @Autowired
    private FoxSqlService foxSqlService;

    @Autowired
    private EntityOptionManager entityOptionManager;


    /**
     * 查询一级列表
     *
     * @param params 用户参数
     * @return 查询结果
     */
    @PostMapping("data")
    public AjaxResult selectDataList(@RequestBody Map<String, Object> params) {
        try {
            String entityType = (String) params.get("entityType");
            String camelField1 = (String) params.get("field1");
            String value1 = (String) params.get("value1");
            String camelField2 = (String) params.get("field2");
            String value = (String) params.get("value");
            String label = (String) params.get("label");


            // 优先查询双参数
            if (!MethodUtils.hasEmpty(entityType, camelField1, value1, camelField2)) {
                String underField1 = StringUtils.camelToUnderline(camelField1);
                String underField2 = StringUtils.camelToUnderline(camelField2);
                String underValue = StringUtils.camelToUnderline(value);

                // 安全性检查：是否对该表和字段进行操作
                if (!this.entityOptionManager.isPermit(entityType, underField1, underField2)) {
                    return AjaxResult.error("不允许该操作！");
                }

                String tableName = this.entityOptionManager.getTableName(entityType);
                if (MethodUtils.hasEmpty(tableName)) {
                    return AjaxResult.error("表名称为空！");
                }

                Object fieldValue1 = value1;
                if (this.entityOptionManager.isNumberField(entityType, underField1)) {
                    fieldValue1 = Long.parseLong(value1);
                }

                if (value == null || label == null) {
                    List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField1, underField2, fieldValue1, true);

                    // 用field1/field2作为distinct的条件，value1作为筛选条件
                    return AjaxResult.success(this.getOptionList(mapList, camelField2, camelField2));
                } else {
                    // 检查：第三个字段是否允许
                    if (!this.entityOptionManager.isPermit(entityType, underValue)) {
                        return AjaxResult.error("不允许该操作！");
                    }

                    // 用field1/field2/value作为distinct的条件，value1作为筛选条件
                    List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField1, underField2, underValue, fieldValue1, true);
                    return AjaxResult.success(this.getOptionList(mapList, value, label));
                }
            }

            // 其次查询单参数
            if (!MethodUtils.hasEmpty(entityType, camelField1)) {
                String underField1 = StringUtils.camelToUnderline(camelField1);

                // 安全性检查：是否对该表和字段进行操作
                if (!this.entityOptionManager.isPermit(entityType, underField1)) {
                    return AjaxResult.error("不允许该操作！");
                }

                String tableName = this.entityOptionManager.getTableName(entityType);
                if (MethodUtils.hasEmpty(tableName)) {
                    return AjaxResult.error("表名称为空！");
                }

                // 查询数据
                List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField1, true);

                // 用field1作为distinct的条件
                if (value == null || label == null) {
                    return AjaxResult.success(this.getOptionList(mapList, camelField1, camelField1));
                } else {
                    return AjaxResult.success(this.getOptionList(mapList, value, label));
                }
            }

            // 不正确的参数
            return AjaxResult.error("操作失败!");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("tree")
    public AjaxResult selectTreeList(@RequestBody Map<String, Object> params) {
        try {
            String entityType = (String) params.get("entityType");
            String camelField1 = (String) params.get("field1");
            String camelField2 = (String) params.get("field2");

            // 优先查询双参数
            if (!MethodUtils.hasEmpty(entityType, camelField1, camelField2)) {
                String underField1 = StringUtils.camelToUnderline(camelField1);
                String underField2 = StringUtils.camelToUnderline(camelField2);

                // 安全性检查：是否对该表和字段进行操作
                if (!this.entityOptionManager.isPermit(entityType, underField1, underField2)) {
                    return AjaxResult.error("不允许该操作！");
                }

                String tableName = this.entityOptionManager.getTableName(entityType);
                if (MethodUtils.hasEmpty(tableName)) {
                    return AjaxResult.error("表名称为空！");
                }


                // 用field1/field1作为distinct的条件
                List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField1, underField2, true);
                return AjaxResult.success(this.getOptionTree(mapList, camelField1, camelField2));
            }

            // 不正确的参数
            return AjaxResult.error("操作失败!");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获得选项
     *
     * @param mapList mapList
     * @param valueField 数值字段
     * @param labelField 标签字段
     * @return 转换结果
     */
    private List<Object> getOptionList(List<Map<String, Object>> mapList, String valueField, String labelField) {
        List<Object> result = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Object dataValue = map.get(valueField);
            Object dataLabel = map.get(labelField);

            Map<String, Object> option = new HashMap<>();
            option.put("value", dataValue);
            option.put("label", dataLabel);

            result.add(option);
        }

        return result;
    }


    /**
     * 获得二级选项
     *
     * @param mapList 数据列表
     * @param field1 字段1
     * @param field2 字段2
     * @return 返回结果
     */
    private List<Object> getOptionTree(List<Map<String, Object>> mapList, String field1, String field2) {
        // 先进行分组
        Map<Object, List<Object>> groupMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object data1 = map.get(field1);
            Object data2 = map.get(field2);

            List<Object> list = groupMap.get(data1);
            if (list == null) {
                list = new ArrayList<>();
                groupMap.put(data1, list);
            }
            list.add(data2);
        }

        List<Object> result = new ArrayList<>();
        for (Object key : groupMap.keySet()) {
            if (key == null) {
                continue;
            }

            // 一级数据
            Map<String, Object> option1 = new HashMap<>();
            option1.put("value", key.toString());
            option1.put("label", key.toString());

            // 二级数据
            List<Object> list = groupMap.get(key);
            if (list == null) {
                continue;
            }

            option1.put("children", new ArrayList<>());
            List option2List = (List) option1.get("children");

            for (Object object2 : list) {
                Map<String, Object> option2 = new HashMap<>();
                option2.put("value", object2.toString());
                option2.put("label", object2.toString());
                option2List.add(option2);
            }

            result.add(option1);
        }

        return result;
    }

}
