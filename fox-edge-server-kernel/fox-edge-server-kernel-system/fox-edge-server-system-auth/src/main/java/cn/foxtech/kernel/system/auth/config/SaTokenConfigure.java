/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.Set;

@SpringBootConfiguration
@EnableWebMvc
public class SaTokenConfigure implements WebMvcConfigurer {
    /**
     * 添加SA-TOKEN的拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义认证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 登录认证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                    SaRouter.match("/**", "/auth/login", r -> StpUtil.checkLogin());
                }).isAnnotation(false))
                //拦截所有接口
                .addPathPatterns("/**")
                //不拦截/user/doLogin登录接口
                .excludePathPatterns("/auth/login");
    }

    /**
     * 为指定包的controller们，添加路由前缀
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        Set<String> packNames = new HashSet<>();
        packNames.add("cn.foxtech.kernel.system.service.controller");
        packNames.add("cn.foxtech.kernel.system.repository.controller");

        // c即具体controller类
        configurer.addPathPrefix("/kernel/manager", c -> {
            // 是否有相关注解啦
            boolean annotationPresent = c.isAnnotationPresent(RestController.class);
            if (!annotationPresent) {
                return false;
            }

            // 包名
            String packName = c.getPackage().getName();
            return packNames.contains(packName);
        });

        // c即具体controller类
        configurer.addPathPrefix("/service/period", c -> {
            // 是否有相关注解啦
            boolean annotationPresent = c.isAnnotationPresent(RestController.class);
            if (!annotationPresent) {
                return false;
            }

            // 包名
            String packName = c.getPackage().getName();
            return packName.equals("cn.foxtech.period.service.controller");
        });


    }

}

