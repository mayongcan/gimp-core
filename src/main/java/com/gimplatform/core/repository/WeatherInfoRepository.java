package com.gimplatform.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gimplatform.core.entity.WeatherInfo;
import com.gimplatform.core.repository.custom.WeatherInfoRepositoryCustom;

/**
 * 天气表操作类
 * @author zzd
 *
 */
@Repository
public interface WeatherInfoRepository extends JpaRepository<WeatherInfo, Long>, WeatherInfoRepositoryCustom{

	/**
	 * 删除RoleFunc关联表
	 * @param roleId
	 */
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM sys_weather_info WHERE CITY =:city AND WEATHER_DATE between :beginTime and :endTime ", nativeQuery = true)
	public void delByCityAndDate(@Param("city")String city, @Param("beginTime")String beginTime, @Param("endTime")String endTime);
}
