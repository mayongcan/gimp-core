package com.gimplatform.core.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 自定义日志配置注解
 * @author zzd
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface LogConf {

    /**
     * 操作类型：1查询 2新增 3编辑 4删除
     * @return
     */
    LogConfOperateType operateType() default LogConfOperateType.NONE;

    /**
     * LOG_DESC
     * @return
     */
    String logDesc() default "";
}
