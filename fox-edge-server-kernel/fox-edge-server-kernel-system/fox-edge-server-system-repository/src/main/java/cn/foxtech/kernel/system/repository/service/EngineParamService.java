package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class EngineParamService {
    private final Map<String, Object> engineParamMap = new HashMap<>();

    public Map<String, Object> getDefault(String operateMode) {
        return (Map<String, Object>) this.engineParamMap.get(operateMode);
    }

    public void initialize() {
        Map<String, Object> exchange = new HashMap<>();
        exchange.put("encode", buildEncodeDefault());
        exchange.put("decode", buildDecodeDefault());
        this.engineParamMap.put("exchange", exchange);

        Map<String, Object> publish = new HashMap<>();
        publish.put("encode", buildEncodeDefault());
        this.engineParamMap.put("publish", publish);

        Map<String, Object> report = new HashMap<>();
        report.put("decode", buildDecodeDefault());
        this.engineParamMap.put("report", report);

        Map<String, Object> keyHandler = new HashMap<>();
        keyHandler.put("decode", buildKeyHandlerDefault());
        this.engineParamMap.put("keyHandler", keyHandler);

        Map<String, Object> splitHandler = new HashMap<>();
        splitHandler.put("decode", buildSplitHandlerDefault());
        this.engineParamMap.put("splitHandler", splitHandler);

        this.engineParamMap.put("include", buildIncludeDefault());
    }

    private Map<String, String> buildEncodeDefault() {
        try {
            String code = this.loadClassPathFile("javascrpit/encodeDefault.js");

            Map<String, String> scriptMethod = new HashMap<>();
            scriptMethod.put("main", "encode");
            scriptMethod.put("code", code);

            return scriptMethod;
        } catch (Exception e) {
            throw new ServiceException("装载缺省的JSP文件错误:" + e.getMessage());
        }
    }

    private Map<String, String> buildDecodeDefault() {
        try {
            String code = this.loadClassPathFile("javascrpit/decodeDefault.js");

            Map<String, String> scriptMethod = new HashMap<>();
            scriptMethod.put("main", "decode");
            scriptMethod.put("code", code);

            return scriptMethod;
        } catch (Exception e) {
            throw new ServiceException("装载缺省的JSP文件错误:" + e.getMessage());
        }
    }

    private Map<String, String> buildKeyHandlerDefault() {
        try {
            String code = this.loadClassPathFile("javascrpit/keyHandlerDefault.js");

            Map<String, String> scriptMethod = new HashMap<>();
            scriptMethod.put("main", "decode");
            scriptMethod.put("code", code);

            return scriptMethod;
        } catch (Exception e) {
            throw new ServiceException("装载缺省的JSP文件错误:" + e.getMessage());
        }
    }

    private Map<String, String> buildSplitHandlerDefault() {
        try {
            String code = this.loadClassPathFile("javascrpit/splitHandlerDefault.js");

            Map<String, String> scriptMethod = new HashMap<>();
            scriptMethod.put("main", "decode");
            scriptMethod.put("code", code);

            return scriptMethod;
        } catch (Exception e) {
            throw new ServiceException("装载缺省的JSP文件错误:" + e.getMessage());
        }
    }

    private String buildIncludeDefault() {
        try {
            String code = this.loadClassPathFile("javascrpit/includeDefault.js");

            return code;
        } catch (Exception e) {
            throw new ServiceException("装载缺省的JSP文件错误:" + e.getMessage());
        }
    }


    private String loadClassPathFile(String classpathFile) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(classpathFile);
        InputStream inputStream = classPathResource.getInputStream();
        return FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);
    }
}
