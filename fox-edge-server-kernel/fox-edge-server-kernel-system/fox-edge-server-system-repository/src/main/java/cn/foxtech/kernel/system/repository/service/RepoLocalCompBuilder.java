package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RepoCompEntity对象的构造器：将MAP参数，转换为RepoCompEntity格式的对象
 */
@Component
public class RepoLocalCompBuilder {
    public RepoCompEntity buildCompEntity(Map<String, Object> params) {
        // 提取业务参数
        String compType = (String) params.get(RepoCompVOFieldConstant.field_comp_type);
        String compRepo = (String) params.get(RepoCompVOFieldConstant.field_comp_repo);
        Map<String, Object> compParam = (Map<String, Object>) params.get(RepoCompVOFieldConstant.field_comp_param);


        // 简单校验参数
        if (MethodUtils.hasNull(compType, compRepo, compParam)) {
            throw new ServiceException("参数不能为空: compType, compRepo, compParam");
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_jar_decoder)) {
           return this.buildJarDecoder(params);
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            return this.buildJspDecoder(params);
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_app_service)) {
            return this.buildAppService(params);
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_file_template)) {
            return this.buildFileTemplate(params);
        }

        throw new ServiceException("不支持的类型:" + compRepo + "," + compType);

    }

    private RepoCompEntity buildJarDecoder(Map<String, Object> params) {
        // 提取业务参数
        String compType = (String) params.get(RepoCompVOFieldConstant.field_comp_type);
        String compRepo = (String) params.get(RepoCompVOFieldConstant.field_comp_repo);
        Map<String, Object> compParam = (Map<String, Object>) params.get(RepoCompVOFieldConstant.field_comp_param);

        // 简单校验参数
        if (MethodUtils.hasNull(compType, compRepo, compParam)) {
            throw new ServiceException("参数不能为空: compType, compRepo, compParam");
        }

        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String fileName = (String) compParam.get(RepoCompConstant.filed_file_name);
        if (MethodUtils.hasNull(manufacturer, deviceType, fileName)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType, fileName");
        }

        // 构造作为参数的实体
        RepoCompEntity entity = new RepoCompEntity();
        entity.setCompRepo(compRepo);
        entity.setCompType(compType);
        entity.setCompName(fileName);
        entity.setCompParam(compParam);

        return entity;
    }

    private RepoCompEntity buildJspDecoder(Map<String, Object> params) {
        // 提取业务参数
        String compType = (String) params.get(RepoCompVOFieldConstant.field_comp_type);
        String compRepo = (String) params.get(RepoCompVOFieldConstant.field_comp_repo);
        Map<String, Object> compParam = (Map<String, Object>) params.get(RepoCompVOFieldConstant.field_comp_param);

        // 简单校验参数
        if (MethodUtils.hasNull(compType, compRepo, compParam)) {
            throw new ServiceException("参数不能为空: compType, compRepo, compParam");
        }

        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        if (MethodUtils.hasNull(manufacturer, deviceType)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType");
        }

        // 构造作为参数的实体
        RepoCompEntity entity = new RepoCompEntity();
        entity.setCompRepo(compRepo);
        entity.setCompType(compType);
        entity.setCompName(manufacturer + ":" + deviceType);
        entity.setCompParam(compParam);

        return entity;
    }

    private RepoCompEntity buildAppService(Map<String, Object> params) {
        // 提取业务参数
        String compType = (String) params.get(RepoCompVOFieldConstant.field_comp_type);
        String compRepo = (String) params.get(RepoCompVOFieldConstant.field_comp_repo);
        Map<String, Object> compParam = (Map<String, Object>) params.get(RepoCompVOFieldConstant.field_comp_param);

        // 简单校验参数
        if (MethodUtils.hasNull(compType, compRepo, compParam)) {
            throw new ServiceException("参数不能为空: compType, compRepo, compParam");
        }

        String appName = (String) compParam.get(ServiceVOFieldConstant.field_app_name);
        String appType = (String) compParam.get(ServiceVOFieldConstant.field_app_type);
        if (MethodUtils.hasNull(appName, appType)) {
            throw new ServiceException("参数不能为空: appName, appType");
        }

        // 构造作为参数的实体
        RepoCompEntity entity = new RepoCompEntity();
        entity.setCompRepo(compRepo);
        entity.setCompType(compType);
        entity.setCompName(appType + ":" + appName);
        entity.setCompParam(compParam);

        return entity;
    }

    private RepoCompEntity buildFileTemplate(Map<String, Object> params) {
        // 提取业务参数
        String compType = (String) params.get(RepoCompVOFieldConstant.field_comp_type);
        String compRepo = (String) params.get(RepoCompVOFieldConstant.field_comp_repo);
        Map<String, Object> compParam = (Map<String, Object>) params.get(RepoCompVOFieldConstant.field_comp_param);

        // 简单校验参数
        if (MethodUtils.hasNull(compType, compRepo, compParam)) {
            throw new ServiceException("参数不能为空: compType, compRepo, compParam");
        }

        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        if (MethodUtils.hasNull(manufacturer, deviceType, modelName)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType, modelName");
        }

        // 构造作为参数的实体
        RepoCompEntity entity = new RepoCompEntity();
        entity.setCompRepo(compRepo);
        entity.setCompType(compType);
        entity.setCompName(modelName);
        entity.setCompParam(compParam);

        // 填写固定参数
        entity.getCompParam().put(RepoCompConstant.filed_model_version, "v1");
        entity.getCompParam().put(RepoCompConstant.filed_version, "1.0.0");

        return entity;
    }
}
