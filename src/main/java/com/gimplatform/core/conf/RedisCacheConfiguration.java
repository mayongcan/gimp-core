package com.gimplatform.core.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gimplatform.core.utils.StringUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis配置
 * @author zzd
 *
 */
@Configuration
@EnableCaching
public class RedisCacheConfiguration extends CachingConfigurerSupport {
	
    Logger logger = LoggerFactory.getLogger(RedisCacheConfiguration.class);

	@Value("${spring.redis.host}")
    private String host;
    
    @Value("${spring.redis.port}")
    private int port;

	@Value("${spring.redis.database}")
    private int database;
    
    @Value("${spring.redis.password}")
    private String redisPassord;
    
    @Value("${spring.redis.timeout}")
    private int timeout;
    
    @Value("${spring.redis.pool.max-active}")
    private int poolMaxActive;
    
    @Value("${spring.redis.pool.max-wait}")
    private long poolMaxWait;
    
    @Value("${spring.redis.pool.max-idle}")
    private int poolMaxIdle;
    
    @Value("${spring.redis.pool.min-idle}")
    private int poolMinIdle;

    @Bean
    public JedisPool redisPoolFactory() {
        logger.info("JedisPool注入成功！！");
        logger.info("redis地址：" + host + ":" + port);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(poolMaxActive);
        jedisPoolConfig.setMaxIdle(poolMaxIdle);
        jedisPoolConfig.setMinIdle(poolMinIdle);
        jedisPoolConfig.setMaxWaitMillis(poolMaxWait);
        JedisPool jedisPool = null;
        if(StringUtils.isBlank(redisPassord))
        	jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, null, database);
        else
        	jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, redisPassord, database);
        return jedisPool;
    }

}