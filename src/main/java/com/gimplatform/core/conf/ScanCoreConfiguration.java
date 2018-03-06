package com.gimplatform.core.conf;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 扫描核心服务包
 * @author zzd
 */
@Configuration
@EnableJpaRepositories(basePackages = { "com.gimplatform.core" })
@EntityScan(basePackages = { "com.gimplatform.core" })
@ComponentScan(basePackages = { "com.gimplatform.core" })
public class ScanCoreConfiguration {

}
