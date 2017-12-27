package com.gimplatform.core.service;

import java.util.List;
import java.util.Map;

/**
 * 用户信息服务类
 * 
 * @author zzd
 *
 */
public interface WeatherInfoService {

	/**
	 * 获取天气列表
	 * @param city
	 * @param beginTime
	 * @param endTime
	 * @param days
	 * @return
	 */
	public List<Map<String, Object>> getWeatherList(String city, String beginTime, String endTime, int days);
}
