package com.gimplatform.core.repository.impl;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.springframework.transaction.annotation.Transactional;
import com.gimplatform.core.common.SqlParams;
import com.gimplatform.core.repository.BaseRepository;
import com.gimplatform.core.repository.custom.WeatherInfoRepositoryCustom;
import com.gimplatform.core.utils.StringUtils;

/**
 * 用户信息DAO实现类
 * @author zzd
 *
 */
@Transactional
public class WeatherInfoRepositoryImpl extends BaseRepository implements WeatherInfoRepositoryCustom{
	
	//用户权限菜单(修改为可获取 不具有子节点的权限)
	private static final String SQL_GET_WEATHER_INFO = "SELECT w.WEATHER_ID as \"weatherId\", w.CITY as \"city\", w.WEATHER_DATE as \"weatherDate\", w.TEXT_DAY as \"textDay\", "
					+ "w.CODE_DAY as \"codeDay\", w.TEXT_NIGTH as \"textNigth\", w.CODE_NIGTH as \"codeNight\", w.HIGHT as \"hight\", w.LOW as \"low\", w.PRECIP as \"precip\", "
					+ "w.WIND_DIRECTION as \"windDirection\", w.WIND_DIRECTION_DEGREE as \"windDirectionDegree\", w.WIND_SPEED as \"windSpeed\", w.WIND_SCALE as \"windScale\" "
			+ "FROM sys_weather_info w "
			+ "WHERE 1 = 1 ";

	@Override
	public List<Map<String, Object>> getWeatherList(Map<String, Object> params) {
		String city = MapUtils.getString(params, "city");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		//组合查询条件
		SqlParams sqlParams = new SqlParams();
		sqlParams.querySql.append(SQL_GET_WEATHER_INFO);
		if(StringUtils.isNotBlank(city)) {
			sqlParams.querySql.append(" AND w.CITY = :city ");
			sqlParams.paramsList.add("city");
			sqlParams.valueList.add(city);
        }
        if(!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
        	sqlParams.querySql.append(" AND w.WEATHER_DATE between :beginTime and :endTime ");
        	sqlParams.paramsList.add("beginTime");
        	sqlParams.paramsList.add("endTime");
        	sqlParams.valueList.add(beginTime);
        	sqlParams.valueList.add(endTime);
        }
        sqlParams.querySql.append(" ORDER BY w.WEATHER_DATE ASC ");
		return getResultList(sqlParams);
	}

}
