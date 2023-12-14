package cn.foxtech.kernel.system.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"cn.foxtech.kernel.system.repository"})
public class SystemRepoConfig {
}
