package cn.foxtech.kernel.system.common.service;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LocalSystemConfService {
    private final List<Map<String, Object>> sensitiveWords = new ArrayList<>();

    @Autowired
    private RedisConsoleService logger;

    public void initialize() {
        try {
            File file = new File("");
            String filePath = FileNameUtils.getOsFilePath(file.getAbsolutePath() + "/conf/fox-edge-server-manager.conf");

            String json = FileTextUtils.readTextFile(filePath, StandardCharsets.UTF_8);
            Map<String, Object> allMap = JsonUtils.buildObject(json, Map.class);

            // 提取敏感词
            List<Map<String, Object>> sensitiveWords = (List<Map<String, Object>>) allMap.getOrDefault("sensitiveWords", List.class);
            this.getSensitiveWords(sensitiveWords);

        } catch (Exception e) {
        }
    }


    private void getSensitiveWords(List<Map<String, Object>> sensitiveWords) {
        for (Map<String, Object> map : sensitiveWords) {
            String src = (String) map.getOrDefault("src", "");
            String dst = (String) map.getOrDefault("dst", "");

            // 检测：参数是否缺失
            if (MethodUtils.hasEmpty(src, dst)) {
                this.logger.error("sensitiveWords的参数缺失： src, dst");
                continue;
            }


            this.sensitiveWords.add(map);
        }
    }

    public void sensitiveWordsStringList(Map<String, Object> entity, String mode, String oldField, String newField) {
        try {
            Object value = entity.get(oldField);
            if (value == null) {
                return;
            }
            if (!(value instanceof List)) {
                return;
            }

            List<String> list = (List<String>) value;

            List<String> clone = new ArrayList<>();
            for (String str : list) {
                // 数据的替换
                String text = str;
                for (Map<String, Object> map : this.sensitiveWords) {
                    String src = (String) map.get("src");
                    String dst = (String) map.get("dst");

                    text = text.replaceAll(src, dst);
                }

                clone.add(text);
            }

            if (mode.equals("replace")) {
                entity.put(oldField, clone);
            }
            if (mode.equals("duplicate")) {
                entity.put(oldField, value);
                entity.put(newField, clone);
            }


        } catch (Exception e) {

        }
    }

    public void sensitiveWordsString(Map<String, Object> entity, String mode, String oldField, String newField) {
        try {
            Object value = entity.get(oldField);
            if (value == null) {
                return;
            }
            if (!(value instanceof String)) {
                return;
            }

            // 数据的替换
            String text = (String) value;
            for (Map<String, Object> map : this.sensitiveWords) {
                String src = (String) map.get("src");
                String dst = (String) map.get("dst");

                text = text.replaceAll(src, dst);
            }

            if (mode.equals("replace")) {
                entity.put(oldField, text);
            }
            if (mode.equals("duplicate")) {
                entity.put(oldField, value);
                entity.put(newField, text);
            }

        } catch (Exception e) {

        }
    }

    public void sensitiveWordsString(List<Map<String, Object>> entityList, String mode, String oldField, String newField) {
        for (Map<String, Object> entity : entityList) {
            this.sensitiveWordsString(entity, mode, oldField, newField);
        }
    }

    public void sensitiveWordsMap(Map<String, Object> entity, String mode, String oldField, String newField) {
        try {
            Object value = entity.get(oldField);
            if (value == null) {
                return;
            }
            if (!(value instanceof Map)) {
                return;
            }

            // 数据的替换
            Map<String, Object> map = (Map<String, Object>) JsonUtils.clone(value);

            if (mode.equals("replace")) {
                entity.put(oldField, map);
            }
            if (mode.equals("duplicate")) {
                entity.put(oldField, value);
                entity.put(newField, map);
            }

        } catch (Exception e) {

        }
    }

    public void sensitiveWordsMap(List<Map<String, Object>> entityList, String mode, String oldField, String newField) {
        for (Map<String, Object> entity : entityList) {
            this.sensitiveWordsMap(entity, mode, oldField, newField);
        }
    }

    public void sensitiveWordsMapString(List<Map<String, Object>> entityList, String mode, String parentField, String oldField, String newField) {
        try {
            for (Map<String, Object> entity : entityList) {
                Object value1 = entity.get(parentField);
                if (value1 == null) {
                    continue;
                }
                if (!(value1 instanceof Map)) {
                    continue;
                }

                Map<String, Object> valueMap = ((Map<String, Object>) value1);

                Object value = valueMap.get(oldField);
                if (value == null) {
                    continue;
                }
                if (!(value instanceof String)) {
                    continue;
                }

                // 数据的替换
                String text = (String) value;
                for (Map<String, Object> map : this.sensitiveWords) {
                    String src = (String) map.get("src");
                    String dst = (String) map.get("dst");

                    text = text.replaceAll(src, dst);
                }

                if (mode.equals("replace")) {
                    valueMap.put(oldField, text);
                }
                if (mode.equals("duplicate")) {
                    valueMap.put(oldField, value);
                    valueMap.put(newField, text);
                }
            }

        } catch (Exception e) {

        }
    }


}
