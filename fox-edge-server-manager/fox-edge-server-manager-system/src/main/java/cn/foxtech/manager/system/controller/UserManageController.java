package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.manager.common.service.EdgeService;
import cn.foxtech.manager.system.constants.Constant;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.entity.constant.UserVOFieldConstant;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.rsa.RSASignature;
import cn.foxtech.common.utils.security.SecurityUtils;
import cn.foxtech.core.domain.AjaxResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/user")
public class UserManageController {
    @Autowired
    private EdgeService edgeService;

    @Autowired
    private EntityManageService entityManageService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(UserEntity.class);

        List<String> filterKeys = EntityVOBuilder.getFilterKeys();
        filterKeys.add(UserVOFieldConstant.field_password);
        return AjaxResult.success(BeanMapUtils.objectToMap(entityList, filterKeys));
    }

    @PostMapping("entities")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, false);
    }

    @PostMapping("page")
    public AjaxResult selectPageList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, true);
    }

    /**
     * 查询实体数据
     *
     * @param body   查询参数
     * @param isPage 是否是分页模式。分页模式，要求有pageNum/pageSize参数，并按分页格式返回
     * @return 实体数据
     */
    private AjaxResult selectEntityList(Map<String, Object> body, boolean isPage) {
        try {
            List<BaseEntity> entityList = this.entityManageService.getEntityList(UserEntity.class, (Object value) -> {
                UserEntity entity = (UserEntity) value;

                boolean result = true;

                if (body.containsKey(UserVOFieldConstant.field_username)) {
                    result = entity.getUsername().contains((String) body.get(UserVOFieldConstant.field_username));
                }
                if (body.containsKey(UserVOFieldConstant.field_role)) {
                    result &= entity.getRole().equals(body.get(UserVOFieldConstant.field_role));
                }
                if (body.containsKey(UserVOFieldConstant.field_permission)) {
                    result &= entity.getPermission().equals(body.get(UserVOFieldConstant.field_permission));
                }
                if (body.containsKey(UserVOFieldConstant.field_menu)) {
                    result &= entity.getPermission().equals(body.get(UserVOFieldConstant.field_menu));
                }

                return result;
            });

            // 获得分页数据
            if (isPage) {
                return PageUtils.getPageList(entityList, body);
            } else {
                return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult getEntity(@QueryParam("username") String username) {
        // 获得用户基本信息
        UserEntity exist = this.entityManageService.getUserEntity(username);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        List<String> filterKeys = EntityVOBuilder.getFilterKeys();
        filterKeys.add(UserVOFieldConstant.field_password);
        Map data = BeanMapUtils.objectToMap(exist, filterKeys);

        return AjaxResult.success(data);
    }

    @GetMapping({"userinfo"})
    public AjaxResult getUserInfo(@QueryParam("username") String username) {
        UserEntity userEntity = this.entityManageService.getUserEntity(username);
        if (userEntity == null) {
            return AjaxResult.error("UserEntity实体不存在");
        }

        UserRoleEntity userRoleEntity = this.entityManageService.getUserRoleEntity(userEntity.getRole());
        UserPermissionEntity userPermissionEntity = this.entityManageService.getUserPermissionEntity(userEntity.getPermission());
        if (MethodUtils.hasNull(userRoleEntity, userPermissionEntity)) {
            return AjaxResult.error("userRoleEntity, userPermissionEntity实体不存在");
        }

        List<String> filterKeys = EntityVOBuilder.getFilterKeys();
        filterKeys.add("password");
        Map data = BeanMapUtils.objectToMap(userEntity, filterKeys);

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", userEntity.getUsername());
        result.put("roles", userRoleEntity.getParams());
        result.put("perms", userPermissionEntity.getParams());
        return AjaxResult.success(result);
    }

    /**
     * 验证是否注册过license
     *
     * @return
     */
    private boolean licenseVerify() {
        try {
            // 获得设备信息
            String cpuId = (String) this.edgeService.getOSInfo().get(Constant.CPUID);

            // 获得公钥
            ClassPathResource classPathResource = new ClassPathResource("/" + "publicKey.keystore");
            InputStream inputStream = classPathResource.getInputStream();
            String publicKey = FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);

            // 检查：是否已经注册
            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, Constant.LICENSE);
            if (configEntity == null) {
                return false;
            }

            // 验证：注册码是否正确
            String license = (String) configEntity.getConfigValue().get(Constant.LICENSE);

            return RSASignature.doCheck(cpuId, license, publicKey);
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping({"usermenu"})
    public AjaxResult getUserMenu(@QueryParam("username") String username) {
        UserMenuEntity userMenuEntity = new UserMenuEntity();

        // 验证：是否注册成功过license
        if (!this.licenseVerify()) {
//            // 如果是未注册，那么装载未注册的菜单界面
//            userMenuEntity = this.entityManageService.getUserMenuEntity("UNREGISTERED");
//            if (MethodUtils.hasNull(userMenuEntity)) {
//                return AjaxResult.error("userMenuEntity实体不存在");
//            }

            // 如果是已注册，那么装载已注册的菜单界面
            UserEntity userEntity = this.entityManageService.getUserEntity(username);
            if (userEntity == null) {
                return AjaxResult.error("UserEntity实体不存在");
            }

            userMenuEntity = this.entityManageService.getUserMenuEntity(userEntity.getMenu());
            if (MethodUtils.hasNull(userMenuEntity)) {
                return AjaxResult.error("userMenuEntity实体不存在");
            }
        } else {
            // 如果是已注册，那么装载已注册的菜单界面
            UserEntity userEntity = this.entityManageService.getUserEntity(username);
            if (userEntity == null) {
                return AjaxResult.error("UserEntity实体不存在");
            }

            userMenuEntity = this.entityManageService.getUserMenuEntity(userEntity.getMenu());
            if (MethodUtils.hasNull(userMenuEntity)) {
                return AjaxResult.error("userMenuEntity实体不存在");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("menus", userMenuEntity.getParams());
        return AjaxResult.success(result);
    }

    @PostMapping("login")
    public AjaxResult login(@RequestBody Map<String, Object> body) {
        try {
            String username = body.get(Constant.USERNAME).toString();
            String password = body.get(Constant.PASSWORD).toString();

            UserEntity existEntity = this.entityManageService.getUserEntity(username);
            if (existEntity == null) {
                return AjaxResult.error("不存在该用户：" + username);
            }

            if (StringUtils.isEmpty(password)) {
                return AjaxResult.error("密码不能为空！");
            }

            if (!SecurityUtils.matchesPassword(password, existEntity.getPassword())) {
                return AjaxResult.error("密码验证不通过！");
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("register")
    public AjaxResult register(@RequestBody Map<String, Object> body) {
        try {
            String username = body.get(Constant.USERNAME).toString();
            String password = body.get(Constant.PASSWORD).toString();

            // 简单校验参数
            if (MethodUtils.hasEmpty(username, password)) {
                return AjaxResult.error("参数不能为空:username, password");
            }

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setUserType("user");
            UserEntity existEntity = this.entityManageService.getUserEntity(username);
            if (existEntity != null) {
                return AjaxResult.error("已经存在该用户：" + username);
            }

            if (StringUtils.isEmpty(password)) {
                return AjaxResult.error("密码不能为空！");
            }

            // 添加记录
            userEntity.setPassword(SecurityUtils.encryptPassword(password));
            this.entityManageService.insertEntity(userEntity);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PutMapping("password")
    public AjaxResult updatePassword(@RequestBody Map<String, Object> body) {
        try {
            String username = body.get(Constant.USERNAME).toString();
            String oldPassword = body.get("oldPassword").toString();
            String newPassword = body.get("newPassword").toString();

            // 简单校验参数
            if (MethodUtils.hasEmpty(username, oldPassword, newPassword)) {
                return AjaxResult.error("参数不能为空:username, oldPassword, newPassword");
            }

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            UserEntity existEntity = this.entityManageService.getUserEntity(username);
            if (existEntity == null) {
                return AjaxResult.error("不存在该用户：" + username);
            }

            if (StringUtils.isEmpty(oldPassword)) {
                return AjaxResult.error("旧密码不能为空！");
            }
            if (StringUtils.isEmpty(newPassword)) {
                return AjaxResult.error("新密码不能为空！");
            }

            if (!SecurityUtils.matchesPassword(oldPassword, existEntity.getPassword())) {
                return AjaxResult.error("密码验证不通过！");
            }

            // 更新记录
            userEntity.setId(existEntity.getId());
            userEntity.setUsername(username);
            userEntity.setUserType(existEntity.getUserType());
            userEntity.setRole(existEntity.getRole());
            userEntity.setPermission(existEntity.getPermission());
            userEntity.setMenu(existEntity.getMenu());
            userEntity.setPassword(SecurityUtils.encryptPassword(newPassword));
            this.entityManageService.updateEntity(userEntity);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        UserEntity exist = this.entityManageService.getEntity(id, UserEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        if (exist.getUserType().endsWith("system")) {
            return AjaxResult.error("系统用户不允许删除！");
        }

        this.entityManageService.deleteEntity(exist);
        return AjaxResult.success();
    }

    @DeleteMapping("entities")
    public AjaxResult deleteEntityList(@QueryParam("ids") String ids) {
        String[] idList = ids.split(",");

        for (String id : idList) {
            if (id == null || id.isEmpty()) {
                continue;
            }

            // 是否为系统用户
            UserEntity existEntity = this.entityManageService.getEntity(Long.parseLong(id), UserEntity.class);
            if (existEntity != null && existEntity.getUserType().endsWith("system")) {
                continue;
            }

            this.entityManageService.deleteEntity(Long.parseLong(id), UserEntity.class);
        }

        return AjaxResult.success();
    }


    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    /**
     * 插入或者更新
     *
     * @param params 参数
     * @return 操作结果
     */
    private AjaxResult insertOrUpdate(Map<String, Object> params) {
        try {
            // 提取业务参数
            String username = (String) params.get(UserVOFieldConstant.field_username);
            String menu = (String) params.get(UserVOFieldConstant.field_menu);
            String role = (String) params.get(UserVOFieldConstant.field_role);
            String permission = (String) params.get(UserVOFieldConstant.field_permission);

            // 简单校验参数
            if (MethodUtils.hasNull(username, menu, role, permission)) {
                return AjaxResult.error("参数不能为空:username, menu, role, permission");
            }

            // 构造作为参数的实体
            UserEntity entity = new UserEntity();
            entity.setUsername(username);
            entity.setPassword(SecurityUtils.encryptPassword("12345678"));
            entity.setMenu(menu);
            entity.setRole(role);
            entity.setPermission(permission);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }

            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                UserEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), UserEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                entity.setUserType("user");
                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                UserEntity exist = this.entityManageService.getEntity(id, UserEntity.class);
                if (exist == null) {
                    return AjaxResult.error("实体不存在");
                }

                // 修改数据
                entity.setId(id);
                entity.setPassword(exist.getPassword());
                entity.setUserType(exist.getUserType());
                this.entityManageService.updateEntity(entity);
                return AjaxResult.success();
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
