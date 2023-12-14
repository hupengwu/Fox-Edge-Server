package cn.foxtech.kernel.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.foxtech.kernel.gateway.domain.AjaxJson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 参考文章：https://blog.csdn.net/weixin_43680776/article/details/123803420
 */
@Configuration
public class SaTokenConfigure {
    @Bean
    public SaReactorFilter getSaServletFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 开放地址
                .addExclude("/favicon.ico")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除/user/login 用于开放登录
                    SaRouter.match("/**", "/auth/login", r -> StpUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
//                    SaRouter.match("/user1/**", r -> StpUtil.checkPermission("user"));
//                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));

                    // ...
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    // 登录过期的出错信息
                    if (e instanceof NotLoginException) {
                        return new SaResult(AjaxJson.CODE_NOT_LOGIN, e.getMessage(), null);
                    }

                    // 其他登录出错信息
                    return SaResult.error(e.getMessage());
                });
    }


    @Bean
    @Primary
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}

