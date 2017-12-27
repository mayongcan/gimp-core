package com.gimplatform.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gimplatform.core.entity.SmsInfo;

@Repository
public interface SmsinfoRepository extends JpaRepository<SmsInfo, Long>, JpaSpecificationExecutor<SmsInfo> {

	/**
	 * 短信编码验证
	 * 
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_sms_info WHERE PHONE=:phone AND SMS_CODE =:smsCode order by  CREATE_DATE desc limit 1", nativeQuery = true)
	public List<SmsInfo> selectByPhoneAndSmsCode(@Param("phone") String phone, @Param("smsCode") String smsCode);

	/**
	 * 判断30分钟内是否有发送过短信
	 * 
	 * @param idList
	 * @return
	 */
	@Query(value = "SELECT * FROM sys_sms_info WHERE PHONE=:phone  AND  TIMESTAMPDIFF(MINUTE, CREATE_DATE,now()) <1", nativeQuery = true)
	public List<SmsInfo> selectByPhone(@Param("phone") String phone);

}
