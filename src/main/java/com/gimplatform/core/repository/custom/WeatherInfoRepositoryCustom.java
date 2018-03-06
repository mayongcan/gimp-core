package com.gimplatform.core.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * 自定义扩展资源类接口
 * @author zzd
 */
@NoRepositoryBean
public interface WeatherInfoRepositoryCustom {

    /**
     * 获取天气列表
     * @param params
     * @return
     */
    public List<Map<String, Object>> getWeatherList(Map<String, Object> params);

}
