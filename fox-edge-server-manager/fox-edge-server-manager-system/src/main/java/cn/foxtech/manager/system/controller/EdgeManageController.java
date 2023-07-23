package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.manager.common.service.EdgeService;
import cn.foxtech.manager.system.constants.Constant;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.rsa.RSASignature;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/edge")
public class EdgeManageController {
    @Autowired
    private EdgeService edgeService;

    @Autowired
    private EntityManageService entityManageService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    @GetMapping("osifo")
    public AjaxResult getOSInfo() {
        return AjaxResult.success(this.edgeService.getOSInfo());
    }

    @GetMapping("license")
    public AjaxResult getLicense() {
        try {
            // 获得设备信息
            String cpuId = (String) this.edgeService.getOSInfo().get(Constant.CPUID);

            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType,Constant.LICENSE);

            String license = "";
            if (configEntity != null) {
                license = (String) configEntity.getConfigValue().get(Constant.LICENSE);
            }

            Map<String, Object> result = new HashMap<>();
            result.put(Constant.CPUID, cpuId);
            result.put(Constant.LICENSE, license);

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("license/verify")
    public AjaxResult licenseVerify() {
        try {
            // 获得设备信息
            String cpuId = (String) this.edgeService.getOSInfo().get(Constant.CPUID);

            // 获得公钥
            File file = ResourceUtils.getFile("/" + "publicKey.keystore");
            String publicKey = FileTextUtils.readTextFile(file);

            // 检查：是否已经注册
            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, Constant.LICENSE);
            if (configEntity == null) {
                throw new ServiceException("该设备尚未注册，设备ID为:" + cpuId);
            }

            // 验证：注册码是否正确
            String license = (String) configEntity.getConfigValue().get(Constant.LICENSE);

            if (!RSASignature.doCheck(cpuId, license, publicKey)) {
                throw new ServiceException("注册码不正确！请重新注册:" + cpuId);
            }

            return AjaxResult.success("设备:[" + cpuId + "]验证通过！");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("license/register")
    public AjaxResult licenseRegister(@RequestBody Map<String, Object> body) {
        try {
            String license = (String) body.get(Constant.LICENSE);

            // 简单校验参数
            if (MethodUtils.hasEmpty(license)) {
                return AjaxResult.error("参数不能为空:license");
            }

            // 获得设备信息
            String cpuId = (String) this.edgeService.getOSInfo().get(Constant.CPUID);

            // 获得公钥
            ClassPathResource classPathResource = new ClassPathResource("/" + "publicKey.keystore");
            InputStream inputStream = classPathResource.getInputStream();
            String publicKey = FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);

            // 验证：输入的注册码
            if (!RSASignature.doCheck(cpuId, license, publicKey)) {
                throw new ServiceException("注册码不正确！请重新注册:" + cpuId);
            }

            // 获得旧的验证码
            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, Constant.LICENSE);
            if (configEntity == null) {
                // 如果不存在：新增数据
                configEntity = new ConfigEntity();
                configEntity.setServiceName(this.foxServiceName);
                configEntity.setServiceType(this.foxServiceType);
                configEntity.setConfigName(Constant.LICENSE);
                configEntity.getConfigValue().put(Constant.LICENSE, license);
                configEntity.getConfigValue().put(Constant.CPUID, cpuId);

                this.entityManageService.insertEntity(configEntity);
            } else {
                // 如果已经存在：覆盖数据
                configEntity = new ConfigEntity();
                configEntity.setServiceName(this.foxServiceName);
                configEntity.setServiceType(this.foxServiceType);
                configEntity.setConfigName(Constant.LICENSE);
                configEntity.getConfigValue().put(Constant.LICENSE, license);
                configEntity.getConfigValue().put(Constant.CPUID, cpuId);

                this.entityManageService.updateEntity(configEntity);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
