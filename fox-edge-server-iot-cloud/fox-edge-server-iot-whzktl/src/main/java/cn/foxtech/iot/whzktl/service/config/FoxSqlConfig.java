package cn.foxtech.iot.whzktl.service.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.entity.service.foxsql"})
@MapperScan({"cn.foxtech.common.entity.service.foxsql"})
public class FoxSqlConfig {
}
