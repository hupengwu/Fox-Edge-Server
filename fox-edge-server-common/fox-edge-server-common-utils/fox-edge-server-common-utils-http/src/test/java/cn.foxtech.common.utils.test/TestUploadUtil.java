package cn.foxtech.common.utils.test;

import cn.foxtech.common.utils.http.UploadUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestUploadUtil {
    public static void main(String[] args) throws IOException {
        String auth = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6ImMyYWI4NzUxLWQ1NTktNDc1MC05NGExLWM0ZGEzMDM2MTEyYyIsInVzZXJuYW1lIjoiYWRtaW4ifQ.RQwi2P4QCwWavwtXNKc611phbToEsCstJ0gX_nXP__iDY8MrHkkF2OSP0TTuFnIRPZm8n3RD00IrD7Rpqm5UNA";

        File file = new File("D:\\我的项目\\fox-edge-server-protocol-gitee\\fox-edge-server-protocol\\fox-edge-server-protocol-mitsubishi-plc-fx-core\\target\\fox-edge-server-protocol-mitsubishi-plc-fx-core-1.0.7.jar");
        String url = "http://192.168.1.21/prod-api/manager/repository/component/upload";
        Map<String, String> headers = new HashMap<>();
        //   headers.put("Accept", "application/json");
        headers.put("Authorization", auth);

        Map<String, Object> formData = new HashMap<>();
        formData.put("modelType", "decoder");
        formData.put("modelName", "fox-edge-server-protocol-mitsubishi-plc-fx-core");
        formData.put("modelVersion", "v1");
        formData.put("component", "service");
        formData.put("file", file);
        UploadUtil.multipartPost(url, headers, formData);
    }


}
