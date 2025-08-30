/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.entity.entity.UserEntity;
import cn.foxtech.common.entity.entity.UserMenuEntity;
import cn.foxtech.common.entity.entity.UserPermissionEntity;
import cn.foxtech.common.entity.entity.UserRoleEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.security.SecurityUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.common.constants.EdgeServiceConstant;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * authManageController
 * 注意：
 * 这个类并不是真正工作的auth接口，这是为了mqtt远程访问的时候，有一个可以响应的restful接口
 * 正在的接口在gateway服务当中，这部分代码是从gateway服务中复制过来的副本
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EdgeService edgeService;

    @GetMapping("login")
    public AjaxResult doLogin(String username, String password) {
        try {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);

            UserEntity existEntity = this.entityManageService.readEntity(userEntity.makeServiceKey(), UserEntity.class);
            if (existEntity == null) {
                throw new RuntimeException("不存在该用户：" + username);
            }

            if (MethodUtils.hasEmpty(password)) {
                throw new RuntimeException("密码不能为空！");
            }

            if (!SecurityUtils.matchesPassword(password, existEntity.getPassword())) {
                throw new RuntimeException("密码验证不通过！");
            }


            Map<String, Object> data = new HashMap<>();
            data.put("tokenType", "token");
            data.put("accessToken", "test");

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error("登录失败！" + e.getMessage());
        }
    }

    @GetMapping({"userinfo"})
    public AjaxResult getUserInfo(String username) {
        try {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);

            UserEntity existUserEntity = this.entityManageService.readEntity(userEntity.makeServiceKey(), UserEntity.class);
            if (existUserEntity == null) {
                throw new RuntimeException("不存在该用户：" + username);
            }

            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setName(existUserEntity.getRole());
            UserRoleEntity exitUserRoleEntity = this.entityManageService.readEntity(userRoleEntity.makeServiceKey(), UserRoleEntity.class);
            if (MethodUtils.hasNull(exitUserRoleEntity)) {
                throw new RuntimeException("userRoleEntity实体不存在");
            }

            UserPermissionEntity userPermissionEntity = new UserPermissionEntity();
            userPermissionEntity.setName(existUserEntity.getPermission());
            UserPermissionEntity exitUserPermissionEntity = this.entityManageService.readEntity(userPermissionEntity.makeServiceKey(), UserPermissionEntity.class);
            if (MethodUtils.hasNull(exitUserPermissionEntity)) {
                throw new RuntimeException(" userPermissionEntity实体不存在");
            }

            List<String> filterKeys = EntityVOBuilder.getFilterKeys();
            filterKeys.add("password");

            Map<String, Object> result = new HashMap<>();
            result.put("nickname", userEntity.getUsername());
            result.put("roles", exitUserRoleEntity.getParams());
            result.put("perms", exitUserPermissionEntity.getParams());

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("查询失败！" + e.getMessage());
        }
    }

    @GetMapping({"usermenu"})
    public AjaxResult getUserMenu(String username) {
        try {

            // 如果是已注册，那么装载已注册的菜单界面
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);

            UserEntity existUserEntity = this.entityManageService.readEntity(userEntity.makeServiceKey(), UserEntity.class);
            if (existUserEntity == null) {
                throw new RuntimeException("不存在该用户：" + username);
            }

            // docker模式下，菜单名称为xxx-DOCKER
            String menuName = existUserEntity.getMenu();
            if (EdgeServiceConstant.value_env_type_docker.equals(this.edgeService.getEnvType())) {
                menuName = menuName + "-DOCKER";
            }

            UserMenuEntity userMenuEntity = new UserMenuEntity();
            userMenuEntity.setName(menuName);
            UserMenuEntity exitUserMenuEntity = this.entityManageService.readEntity(userMenuEntity.makeServiceKey(), UserMenuEntity.class);
            if (MethodUtils.hasNull(exitUserMenuEntity)) {
                throw new RuntimeException("userMenuEntity实体不存在");
            }


            Map<String, Object> result = new HashMap<>();
            result.put("menus", exitUserMenuEntity.getParams());

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("查询失败！" + e.getMessage());
        }
    }

    // 查询 Token 信息  ---- http://localhost:9000/user/tokenInfo
    @GetMapping("tokenInfo")
    public AjaxResult tokenInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("tokenName", "test");
        data.put("tokenValue", "test");
        data.put("isLogin", true);
        data.put("loginId", "admin");
        data.put("loginType", "login");
        data.put("tokenTimeout", 3600 * 24 * 30);
        data.put("sessionTimeout", 3600 * 24 * 30);
        data.put("tokenSessionTimeout", -2);
        data.put("tokenActiveTimeout", -1);
        data.put("loginDevice", "default-device");
        data.put("tag", null);


        return AjaxResult.success(data);
    }

    // 测试注销  ---- http://localhost:9000/user/logout
    @DeleteMapping("logout")
    public AjaxResult logout() {
        return AjaxResult.success("");
    }
}
