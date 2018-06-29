package com.gimplatform.core.conf;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.gimplatform.core.filter.PostParamsHandleFilter;

/**
 * 扫描核心服务包
 * @author zzd
 */
@Configuration
@EnableJpaRepositories(basePackages = { "com.gimplatform.core" })
@EntityScan(basePackages = { "com.gimplatform.core" })
@ComponentScan(basePackages = { "com.gimplatform.core" })
public class ScanCoreConfiguration {
    
    /**
     * 添加过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new PostParamsHandleFilter());
        registration.addUrlPatterns("/*");
        registration.setName("postParamsHandleFilter");
        registration.setOrder(1);
        return registration;
    }

}
